package com.moddamage.variables.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.backend.ArmorSet;
import com.moddamage.backend.ExternalPluginManager;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.StringExp;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class PlayerString extends StringExp<Player>
{
	private static Pattern pattern = Pattern.compile("_("+ Utils.joinBy("|", PlayerStringProperty.values()) +")", Pattern.CASE_INSENSITIVE);
	
	public enum PlayerStringProperty
	{
		ARMORSET
		{
			@Override protected String getString(Player player)
			{
				return new ArmorSet(player).toString();
			}
		},
		GROUP
		{
			@Override protected String getString(Player player)
			{
				return ExternalPluginManager.getGroupsManager().getGroups(player).toString();
			}
		},
		WIELDING
		{
			@Override protected String getString(Player player)
			{
				return player.getItemInHand().getType().name();
			}
		},
		ENCHANTMENTS
		{
			@Override protected String getString(Player player)
			{
				return HELD_ENCHANTMENTS.getString(player) + " " + ARMOR_ENCHANTMENTS.getString(player);
			}
		},
		HELD_ENCHANTMENTS
		{
			@Override protected String getString(Player player)
			{
				return player.getItemInHand().getEnchantments().toString();
			}
		},
		ARMOR_ENCHANTMENTS
		{
			@Override protected String getString(Player player)
			{
				return HELMET_ENCHANTMENTS.getString(player) + " " + 
					   CHESTPLATE_ENCHANTMENTS.getString(player) + " " + 
					   LEGGINGS_ENCHANTMENTS.getString(player) + " " + 
					   BOOTS_ENCHANTMENTS.getString(player);
			}
		},
		HELMET_ENCHANTMENTS
		{
			@Override protected String getString(Player player)
			{
				return player.getInventory().getHelmet().getEnchantments().toString();
			}
		},
		CHESTPLATE_ENCHANTMENTS
		{
			@Override protected String getString(Player player)
			{
				return player.getInventory().getChestplate().getEnchantments().toString();
			}
		},
		LEGGINGS_ENCHANTMENTS
		{
			@Override protected String getString(Player player)
			{
				return player.getInventory().getLeggings().getEnchantments().toString();
			}
		},
		BOOTS_ENCHANTMENTS
		{
			@Override protected String getString(Player player)
			{
				return player.getInventory().getBoots().getEnchantments().toString();
			}
		};
		
		abstract protected String getString(Player player);
	}
	

	private final PlayerStringProperty propertyMatch;
	
	public PlayerString(IDataProvider<Player> playerDP, PlayerStringProperty propertyMatch)
	{
		super(Player.class, playerDP);
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public String get(Player player, EventData data)
	{
		return propertyMatch.getString(player);
	}
	
	public static void register()
	{
		DataProvider.register(String.class, Player.class, pattern, new IDataParser<String, Player>()
			{
				@Override
				public IDataProvider<String> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					sm.accept();
					return new PlayerString(playerDP, PlayerStringProperty.valueOf(m.group(1).toUpperCase()));
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP + "_" + propertyMatch.name().toLowerCase();
	}
}
