package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicEnchantmentInteger extends DynamicInteger
{	
	public enum EnchantmentItemSlot
	{
		ANY(false) {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				return Math.max(HELD.getEnchantmentLevel(player, enchantment), 
								ARMOR.getEnchantmentLevel(player, enchantment));
			}
		},
		HELD(true) {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getItemInHand();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
			@Override void setEnchantmentLevel(Player player, Enchantment enchantment, int level)
			{
				ItemStack item = player.getItemInHand();
				if (item != null) item.addEnchantment(enchantment, level);
			}
		},
		ARMOR(false) {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				return Math.max(HELMET.getEnchantmentLevel(player, enchantment), 
					   Math.max(CHESTPLATE.getEnchantmentLevel(player, enchantment),
					   Math.max(LEGGINGS.getEnchantmentLevel(player, enchantment), 
							    BOOTS.getEnchantmentLevel(player, enchantment))));
			}
		},
		HELMET(true) {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getHelmet();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
			@Override void setEnchantmentLevel(Player player, Enchantment enchantment, int level)
			{
				ItemStack item = player.getInventory().getHelmet();
				if (item != null) item.addEnchantment(enchantment, level);
			}
		},
		CHESTPLATE(true) {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getChestplate();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
			@Override void setEnchantmentLevel(Player player, Enchantment enchantment, int level)
			{
				ItemStack item = player.getInventory().getChestplate();
				if (item != null) item.addEnchantment(enchantment, level);
			}
		},
		LEGGINGS(true) {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getLeggings();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
			@Override void setEnchantmentLevel(Player player, Enchantment enchantment, int level)
			{
				ItemStack item = player.getInventory().getLeggings();
				if (item != null) item.addEnchantment(enchantment, level);
			}
		},
		BOOTS(true) {
			@Override int getEnchantmentLevel(Player player, Enchantment enchantment)
			{
				ItemStack item = player.getInventory().getBoots();
				return item == null? 0 : item.getEnchantmentLevel(enchantment);
			}
			@Override void setEnchantmentLevel(Player player, Enchantment enchantment, int level)
			{
				ItemStack item = player.getInventory().getBoots();
				if (item != null) item.addEnchantment(enchantment, level);
			}
		};
		
		public final boolean settable;
		
		private EnchantmentItemSlot(boolean settable)
		{
			this.settable = settable;
		}
		
		abstract int getEnchantmentLevel(Player player, Enchantment enchantment);
		void setEnchantmentLevel(Player player, Enchantment enchantment, int level) {}
		
		public static final String regexString = Utils.joinBy("|", EnchantmentItemSlot.values());
	}
	
	public static void register()
	{
		String enchantmentRegexString = "";
		boolean first = true;
		for (Enchantment enchantment : Enchantment.values())
		{
			if (first) first = false;
			else enchantmentRegexString += "|";
			enchantmentRegexString += enchantment.getName();
		}
		DynamicInteger.register(
				Pattern.compile("([a-z]+)(?:_("+ EnchantmentItemSlot.regexString
						+"))?_enchantmentlevel_("+ enchantmentRegexString +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
						if (entityRef == null) return null;
						String slot = matcher.group(2);
						if (slot == null) slot = "ANY";
						sm.accept();
						return sm.acceptIf(new DynamicEnchantmentInteger(
								entityRef, 
								EnchantmentItemSlot.valueOf(slot.toUpperCase()),
								Enchantment.getByName(matcher.group(3).toUpperCase())));
					}
				});
	}
	
	protected final DataRef<Entity> entityRef;
	protected final EnchantmentItemSlot itemSlot;
	protected final Enchantment enchantment;
	
	DynamicEnchantmentInteger(DataRef<Entity> entityRef, EnchantmentItemSlot itemSlot, Enchantment enchantment)
	{
		this.entityRef = entityRef;
		this.itemSlot = itemSlot;
		this.enchantment = enchantment;
	}
	
	
	@Override
	public int getValue(EventData data)
	{
		Entity entity = entityRef.get(data);
		if(entity instanceof Player)
		{
			Player player = (Player)entity;
			return itemSlot.getEnchantmentLevel(player, enchantment);
		}
		return 0;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		Entity entity = entityRef.get(data);
		if(entity instanceof Player)
		{
			Player player = (Player)entity;
			
			// lock the enchantment value inside the acceptable range
			value = Math.min(Math.max(enchantment.getStartLevel(), value), enchantment.getMaxLevel());
			
			itemSlot.setEnchantmentLevel(player, enchantment, value);
		}
	}
	
	@Override
	public boolean isSettable()
	{
		return itemSlot.settable;
	}
	
	@Override
	public String toString()
	{
		return entityRef + "_" + itemSlot.name().toLowerCase() + "_enchantmentlevel_" + enchantment.getName();
	}
}