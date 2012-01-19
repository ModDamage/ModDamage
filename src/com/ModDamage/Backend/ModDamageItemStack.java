package com.ModDamage.Backend;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class ModDamageItemStack
{
	public static final String itemStackPart = "(\\w+\\*" + DynamicInteger.dynamicIntegerPart + ")";
	final Material material;
	final DynamicInteger amount;
	private Map<Enchantment, DynamicInteger> enchantments;
	private int lastAmount;
	private Map<Enchantment, Integer> lastEnchants;
	
	private ModDamageItemStack(Material material, DynamicInteger number)
	{
		this.material = material;
		this.amount = number;
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
	
	public void updateAmount(TargetEventInfo eventInfo)
	{
		lastAmount = amount.getValue(eventInfo);
		if (enchantments != null)
		{
			for (Entry<Enchantment, DynamicInteger> entry : enchantments.entrySet())
				lastEnchants.put(entry.getKey(), entry.getValue().getValue(eventInfo));
		}
	}
	
	public boolean contains(ItemStack itemStack)
	{
		return material.equals(itemStack.getType()) && lastAmount >= itemStack.getAmount();
	}
	
	public ItemStack toItemStack()
	{
		ItemStack item = new ItemStack(material, lastAmount);
		
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
	
	public static ModDamageItemStack getNew(String string)
	{
		String[] parts = string.split("\\*");
		if (parts.length == 0 || parts.length > 2) return null;
		
		Material material;
		//for(Material someMaterial : Material.values())
		//	if(parts[0].equalsIgnoreCase(someMaterial.name()))
		//		material = someMaterial;
		try
		{
			material = Material.valueOf(parts[0].toUpperCase());
		}
		catch (IllegalArgumentException e){
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: unable to match material \"" + parts[0] + "\"");
			return null;
		}
		
		DynamicInteger integer;
		if(parts.length == 2)
			integer = DynamicInteger.getNew(parts[1]);
		else
			integer = DynamicInteger.getNew("1");
			
		if(material != null && integer != null)
			return new ModDamageItemStack(material, integer);
		
		return null;
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
