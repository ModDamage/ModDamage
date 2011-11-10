package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class ModDamageItemStack
{
	public static final String itemStackPart = "(\\w+\\*" + DynamicInteger.dynamicIntegerPart + ")";
	final Material material;
	final DynamicInteger number;
	private int lastValue;
	
	private ModDamageItemStack(Material material, DynamicInteger number)
	{
		this.material = material;
		this.number = number;
	}
	
	public void updateAmount(TargetEventInfo eventInfo)
	{
		lastValue = number.getValue(eventInfo);
	}
	
	public boolean contains(ItemStack itemStack)
	{
		return material.equals(itemStack.getType()) && lastValue >= itemStack.getAmount();
	}
	
	public ItemStack toItemStack()
	{
		return new ItemStack(material, lastValue);
	}
	
	public static ModDamageItemStack getNew(String string)
	{
		String[] parts = string.split("\\*");
		if(parts.length == 2)
		{
			Material material = null;
			for(Material someMaterial : Material.values())
				if(parts[0].equalsIgnoreCase(someMaterial.name()))
					material = someMaterial;
			if(material == null) ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: unable to match material \"" + parts[0] + "\"", LoadState.FAILURE);
			DynamicInteger integer = DynamicInteger.getNew(parts[1]);
			if(material != null && integer != null)
				return new ModDamageItemStack(material, integer);
		}
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
		return material.name() + "*" + number.toString();
	}
}
