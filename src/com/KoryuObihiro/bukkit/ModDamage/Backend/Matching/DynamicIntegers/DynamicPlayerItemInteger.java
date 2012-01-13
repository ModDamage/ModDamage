package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicPlayerItemInteger extends DynamicInteger
{
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("("+ Utils.joinBy("|", EntityReference.values()) +")_("+ 
									 Utils.joinBy("|", PlayerItemTarget.values()) +")_durability", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DIResult getNewFromFront(Matcher matcher, String rest)
					{
						return new DIResult(new DynamicPlayerItemInteger(
								EntityReference.valueOf(matcher.group(1).toUpperCase()), 
								PlayerItemTarget.valueOf(matcher.group(2).toUpperCase())), rest);
					}
				});
	}
	
	enum PlayerItemTarget {
		WIELDED {
			public ItemStack getItem(Player player) {
				return player.getItemInHand();
			}
		},
		HELMET {
			public ItemStack getItem(Player player) {
				return player.getInventory().getHelmet();
			}
		},
		CHESTPLATE {
			public ItemStack getItem(Player player) {
				return player.getInventory().getChestplate();
			}
		},
		LEGGINGS {
			public ItemStack getItem(Player player) {
				return player.getInventory().getLeggings();
			}
		},
		BOOTS {
			public ItemStack getItem(Player player) {
				return player.getInventory().getBoots();
			}
		};
		
		public abstract ItemStack getItem(Player player);
	}
	
	EntityReference entityReference;
	PlayerItemTarget playerItemTarget;

	public DynamicPlayerItemInteger(EntityReference entityReference, PlayerItemTarget playerItemTarget)
	{
		this.entityReference = entityReference;
		this.playerItemTarget = playerItemTarget;
	}

	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if (entity instanceof Player)
			return playerItemTarget.getItem((Player)entity).getDurability();
		return 0;
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if (entity instanceof Player)
			playerItemTarget.getItem((Player)entity).setDurability((short) value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}

}
