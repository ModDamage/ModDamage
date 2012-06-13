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
import com.ModDamage.Alias.MaterialAliaser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Variables.Ints.Constant;

public class ModDamageItemStack
{
	private final Material material;
	private final IntegerExp data, amount;
	private Map<Enchantment, IntegerExp> enchantments;
	private int lastData, lastAmount;
	private Map<Enchantment, Integer> lastEnchants;
	
	private ModDamageItemStack(Material material, IntegerExp data, IntegerExp amount)
	{
		this.material = material;
		this.data = data;
		this.amount = amount;
	}
	
	public void addEnchantment(Enchantment enchantment, IntegerExp level)
	{
		if (enchantments == null)
		{
			enchantments = new HashMap<Enchantment, IntegerExp>(2);
			lastEnchants = new HashMap<Enchantment, Integer>(2);
		}
		enchantments.put(enchantment, level);
	}
	
	public void update(EventData data) throws BailException
	{
		if (this.data != null)
			lastData = this.data.getValue(data);
		lastAmount = amount.getValue(data);
		if (enchantments != null)
		{
			for (Entry<Enchantment, IntegerExp> entry : enchantments.entrySet())
				lastEnchants.put(entry.getKey(), entry.getValue().getValue(data));
		}
	}
	
	public boolean matches(ItemStack itemStack)
	{
		if (itemStack == null)
			return material == Material.AIR;
		
		return material.equals(itemStack.getType()) && lastAmount <= itemStack.getAmount() &&
				(data == null? true : lastData == itemStack.getDurability());
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
		Material first = null;
		
		if (materials != null && materials.size() > 0)
			first = materials.iterator().next();
			
		if (first == null)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: unable to match material \"" + m.group() + "\"");
			return null;
		}
		
		if (materials.size() > 1)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: matched "+materials.size()+" materials, wanted only one: \"" + m.group() + "\"");
			return null;
		}
		
		Material material = first;
		
		IntegerExp data;
		if (sm.matchesFront("@"))
		{
			data = IntegerExp.getIntegerFromFront(sm.spawn(), info);
			if (data == null) return null;
		}
		else
			data = null;
		

		IntegerExp amount;
		if (sm.matchesFront("*"))
		{
			amount = IntegerExp.getIntegerFromFront(sm.spawn(), info);
			if (amount == null) return null;
		}
		else
			amount = new Constant(1);
		
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
