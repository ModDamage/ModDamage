package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnchantmentsRef;
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
		DynamicInteger.register(
				Pattern.compile("(\\w+)(?:_("+ EnchantmentItemSlot.regexString
						+"))?_enchant(?:ment)?_?level_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase()); if (entityRef == null) return null;
						String slot = matcher.group(2);
						if (slot == null) slot = "ANY";
						Enchantment enchantment = Enchantment.getByName(matcher.group(3).toUpperCase());
						if (enchantment == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown enchantment named \"" + matcher.group(3) + "\"");
							return null;
						}
						return sm.acceptIf(new DynamicEnchantmentInteger(
								entityRef, 
								EnchantmentItemSlot.valueOf(slot.toUpperCase()),
								enchantment));
					}
				});
		DynamicInteger.register(
				Pattern.compile("enchant(?:ment)?_?level_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						DataRef<EnchantmentsRef> enchantmentsRef = info.get(EnchantmentsRef.class, "-enchantments");
						if (enchantmentsRef == null) return null;
						Enchantment enchantment = Enchantment.getByName(matcher.group(1).toUpperCase());
						if (enchantment == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown enchantment named \"" + matcher.group(1) + "\"");
							return null;
						}
						return sm.acceptIf(new DynamicEnchantmentInteger(
								enchantmentsRef,
								enchantment));
					}
				});
	}
	
	private final DataRef<Entity> entityRef;
	private final DataRef<EnchantmentsRef> enchantmentsRef;
	private final EnchantmentItemSlot itemSlot;
	private final Enchantment enchantment;
	
	DynamicEnchantmentInteger(DataRef<Entity> entityRef, EnchantmentItemSlot itemSlot, Enchantment enchantment)
	{
		this.entityRef = entityRef;
		this.itemSlot = itemSlot;
		this.enchantment = enchantment;
		this.enchantmentsRef = null;
	}
	DynamicEnchantmentInteger(DataRef<EnchantmentsRef> enchantmentsRef, Enchantment enchantment)
	{
		this.entityRef = null;
		this.itemSlot = null;
		this.enchantment = enchantment;
		this.enchantmentsRef = enchantmentsRef;
	}
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		if (entityRef != null)
		{
			Entity entity = entityRef.get(data);
			if(entity instanceof Player)
			{
				Player player = (Player)entity;
				return itemSlot.getEnchantmentLevel(player, enchantment);
			}
		}
		else
		{
			Integer level = enchantmentsRef.get(data).map.get(enchantment);
			return level == null? 0 : level;
		}
		
		return 0;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		// lock the enchantment value inside the acceptable range
		value = Math.min(Math.max(enchantment.getStartLevel(), value), enchantment.getMaxLevel());
		
		if (entityRef != null)
		{
			Entity entity = entityRef.get(data);
			if(entity instanceof Player)
			{
				Player player = (Player)entity;
				
				itemSlot.setEnchantmentLevel(player, enchantment, value);
			}
		}
		else
		{
			enchantmentsRef.get(data).map.put(enchantment, value);
		}
	}
	
	@Override
	public boolean isSettable()
	{
		return itemSlot != null? itemSlot.settable : true;
	}
	
	@Override
	public String toString()
	{
		if (entityRef != null)
			return entityRef + "_" + itemSlot.name().toLowerCase() + "_enchantmentlevel_" + enchantment.getName();
		return "enchantmentlevel_" + enchantment.getName();
	}
}