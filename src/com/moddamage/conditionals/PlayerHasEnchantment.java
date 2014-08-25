package com.moddamage.conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.alias.EnchantmentAliaser;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class PlayerHasEnchantment extends Conditional<Player>
{
	public static final Pattern pattern = Pattern.compile("\\.hasenchantment\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	protected final Collection<Enchantment> enchantments;
	
	public PlayerHasEnchantment(IDataProvider<Player> playerDP, Collection<Enchantment> enchantments)
	{
		super(Player.class, playerDP);
		this.enchantments = enchantments;
	}

	@Override
	public Boolean get(Player player, EventData data)
	{
		for(Enchantment enchantment : enchantments)
			if(player.getItemInHand().containsEnchantment(enchantment))
				return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return startDP + ".hasenchantment." + Utils.joinBy(",", enchantments);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Player.class, pattern, new IDataParser<Boolean, Player>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Player> playerDP, Matcher m, StringMatcher sm)
				{
					Collection<Enchantment> enchantments = EnchantmentAliaser.match(m.group(1));
					if(enchantments.isEmpty())	return null;
					
					return new PlayerHasEnchantment(playerDP, enchantments);
				}
			});
	}
}
