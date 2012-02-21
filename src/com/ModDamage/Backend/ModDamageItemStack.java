package com.ModDamage.Backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.Aliasing.MaterialAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Backend.Matching.DynamicIntegers.ConstantInteger;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ModDamageItemStack
{
	public static final String itemStackPart = "(\\w+\\*" + DynamicInteger.dynamicIntegerPart + ")";
	private final Material material;
	private final DynamicInteger data, amount;
	private Map<Enchantment, DynamicInteger> enchantments;
	private int lastData, lastAmount;
	private Map<Enchantment, Integer> lastEnchants;
	
	private ModDamageItemStack(Material material, DynamicInteger data, DynamicInteger amount)
	{
		this.material = material;
		this.data = data;
		this.amount = amount;
	}
	
	public void addEnchantment(Enchantment enchantment, DynamicInteger level)
	{
		if (enchantments == null)
		{
			enchantments = new HashMap<Enchantment, DynamicInteger>(2);
			lastEnchants = new HashMap<Enchantment, Integer>(2);
		}
		enchantments.put(enchantment, level);
	}
	
	public void updateAmount(EventData data)
	{
		if (this.data != null)
			lastData = this.data.getValue(data);
		lastAmount = amount.getValue(data);
		if (enchantments != null)
		{
			for (Entry<Enchantment, DynamicInteger> entry : enchantments.entrySet())
				lastEnchants.put(entry.getKey(), entry.getValue().getValue(data));
		}
	}
	
	public boolean contains(ItemStack itemStack)
	{
		return material.equals(itemStack.getType()) && lastAmount >= itemStack.getAmount();
	}
	
	public ItemStack toItemStack()
	{
		ItemStack item = new ItemStack(material, lastAmount, (short) 0, Byte.valueOf((byte) lastData));
		
		if (lastEnchants != null)
		{
			try
			{
				item.addEnchantments(lastEnchants);
			}
			catch (IllegalArgumentException e)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, e.getMessage());
			}
		}
		
		return item;
	}
	
	public static final Pattern materialPattern = Pattern.compile("(\\w+)(?=[@*]|$)"); // word followed by @ * or nothing
	
	public static ModDamageItemStack getNewFromFront(EventInfo info, StringMatcher sm)
	{
		Matcher m = sm.matchFront(materialPattern);
		if (m == null) return null;
		
		Collection<Material> materials = MaterialAliaser.match(m.group());
		if (materials == null || materials.size() == 0)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: unable to match material \"" + m.group() + "\"");
			return null;
		}
		
		if (materials.size() > 1)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: matched too many materials: \"" + m.group() + "\"");
			return null;
		}
		
		Material material = materials.iterator().next();
		
		DynamicInteger data;
		if (sm.matchesFront("@"))
		{
			data = DynamicInteger.getIntegerFromFront(sm.spawn(), info);
			if (data == null) return null;
		}
		else
			data = null;
		

		DynamicInteger amount;
		if (sm.matchesFront("*"))
		{
			amount = DynamicInteger.getIntegerFromFront(sm.spawn(), info);
			if (amount == null) return null;
		}
		else
			amount = new ConstantInteger(1);
		
		return sm.acceptIf(new ModDamageItemStack(material, data, amount));
	}

	public static ItemStack[] toItemStacks(Collection<ModDamageItemStack> items)
	{
		ItemStack[] stacks = new ItemStack[items.size()];
		int i = 0;
		for(ModDamageItemStack item : items)
		{
			stacks[i] = item.toItemStack();
			i++;
		}
		return stacks;
	}
	
	@Override
	public String toString()
	{
		return material.name() + "*" + amount.toString();
	}
}
