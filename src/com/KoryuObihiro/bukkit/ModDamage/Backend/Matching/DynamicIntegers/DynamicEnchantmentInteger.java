package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.Utils;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicEnchantmentInteger extends DynamicInteger
{	
	public enum EnchantmentItemSlot
	{
		ANY {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				return Math.max(HELD.getEnchantmentLevel(player, enchantment), 
								ARMOR.getEnchantmentLevel(player, enchantment));
			}
		},
		HELD {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getItemInHand();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
		},
		ARMOR {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				return Math.max(HELMET.getEnchantmentLevel(player, enchantment), 
					   Math.max(CHESTPLATE.getEnchantmentLevel(player, enchantment),
					   Math.max(LEGGINGS.getEnchantmentLevel(player, enchantment), 
							    BOOTS.getEnchantmentLevel(player, enchantment))));
			}
		},
		HELMET {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getHelmet();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
		},
		CHESTPLATE {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getChestplate();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
		},
		LEGGINGS {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getLeggings();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
		},
		BOOTS {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getBoots();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
		};
		
		abstract int getEnchantmentLevel(Player player, Enchantment enchantment);
		
		public static final String regexString = Utils.joinBy("|", EnchantmentItemSlot.values());
	}
	
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("("+ EntityReference.regexString +")(?:_("+ EnchantmentItemSlot.regexString
						+"))?_enchantmentlevel_("+ Utils.joinBy("|", Enchantment.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DIResult getNewFromFront(Matcher matcher, String rest)
					{
						String slot = matcher.group(2);
						if (slot == null) slot = "ANY";
						return new DIResult(new DynamicEnchantmentInteger(
								EntityReference.valueOf(matcher.group(1).toUpperCase()), 
								EnchantmentItemSlot.valueOf(slot.toUpperCase()),
								Enchantment.getByName(matcher.group(3).toUpperCase())), rest);
					}
				});
	}
	
	protected final EntityReference entityReference;
	protected final EnchantmentItemSlot itemSlot;
	protected final Enchantment enchantment;
	
	DynamicEnchantmentInteger(EntityReference reference, EnchantmentItemSlot itemSlot, Enchantment enchantment)
	{
		this.entityReference = reference;
		this.itemSlot = itemSlot;
		this.enchantment = enchantment;
	}
	
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.PLAYER))
		{
			Player player = (Player)entityReference.getEntity(eventInfo);
			return itemSlot.getEnchantmentLevel(player, enchantment);
		}
		return 0;
	}
	
	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "_" + itemSlot.name().toLowerCase() + "_enchantmentlevel_" + enchantment.getName();
	}
}