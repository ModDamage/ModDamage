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

import com.ModDamage.LogUtil;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.StringMatcher;
import com.ModDamage.Alias.MaterialAliaser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.LiteralNumber;

public class ModDamageItemStack
{
	private final Material material;
	private final IDataProvider<Integer> data;
	private final IDataProvider<? extends Number> amount;
	private Map<Enchantment, IDataProvider<Integer>> enchantments;
	private int lastData, lastAmount;
	private Map<Enchantment, Integer> lastEnchants;
	
	private ModDamageItemStack(Material material, IDataProvider<Integer> data, IDataProvider<? extends Number> amount)
	{
		this.material = material;
		this.data = data;
		this.amount = amount;
	}
	
	public void addEnchantment(Enchantment enchantment, IDataProvider<Integer> level)
	{
		if (enchantments == null)
		{
			enchantments = new HashMap<Enchantment, IDataProvider<Integer>>(2);
			lastEnchants = new HashMap<Enchantment, Integer>(2);
		}
		enchantments.put(enchantment, level);
	}
	
	public void update(EventData data) throws BailException
	{
		if (this.data != null) {
			Integer d = this.data.get(data);
			if (d != null)
				lastData = d;
			else
				lastData = 0;
		}
		
		Number am = amount.get(data);
		if (am != null)
			lastAmount = am.intValue();
		else
			lastAmount = 0;
		
		if (enchantments != null)
		{
			for (Entry<Enchantment, IDataProvider<Integer>> entry : enchantments.entrySet())
				lastEnchants.put(entry.getKey(), entry.getValue().get(data));
		}
	}
	
	public boolean typeMatches(ItemStack itemStack)
	{
		if (itemStack == null)
			return material == Material.AIR;
		
		if (!material.equals(itemStack.getType()))
			return false;
		
		if (material == Material.AIR)
			// ignore data for air
			return true;
		
		return data == null? true : itemStack.getDurability() == lastData;
	}
	
	public boolean matches(ItemStack itemStack)
	{
		if (!typeMatches(itemStack)) return false;
		
		if (material == Material.AIR)
			// ignore amount for air
			return true;
		
		return itemStack.getAmount() >= lastAmount;
	}
	
	public ItemStack toItemStack()
	{
		ItemStack item = new ItemStack(material, lastAmount, (short) lastData);
		
		if (lastEnchants != null)
		{
			try
			{
				item.addEnchantments(lastEnchants);
			}
			catch (IllegalArgumentException e)
			{
				LogUtil.error(e.getMessage());
			}
		}
		
		return item;
	}
	
	public static final Pattern materialPattern = Pattern.compile("(\\w+)(?=[@*]|$)"); // word followed by @ * or nothing
	
	public static ModDamageItemStack getNewFromFront(ScriptLine line, EventInfo info, StringMatcher sm)
	{
		Matcher m = sm.matchFront(materialPattern);
		if (m == null) return null;
		
		Collection<Material> materials = MaterialAliaser.match(line, m.group());
		Material first = null;
		
		if (materials != null && materials.size() > 0)
			first = materials.iterator().next();
			
		if (first == null)
		{
			LogUtil.error(line, "Error: unable to match material \"" + m.group() + "\"");
			return null;
		}
		
		if (materials == null || materials.size() > 1)
		{
			LogUtil.error(line, "Error: matched "+(materials == null? 0:materials.size())+" materials, wanted only one: \"" + m.group() + "\"");
			return null;
		}
		
		Material material = first;
		
		IDataProvider<Integer> data;
		if (sm.matchesFront("@"))
		{
			data = DataProvider.parse(line, info, Integer.class, sm.spawn());
			if (data == null) return null;
		}
		else
			data = null;
		

		IDataProvider<? extends Number> amount;
		if (sm.matchesFront("*"))
		{
			amount = DataProvider.parse(line, info, Integer.class, sm.spawn());
			if (amount == null) return null;
		}
		else
			amount = new LiteralNumber(1);
		
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
	
	public int getAmount() {
		return lastAmount;
	}
	
	@Override
	public String toString()
	{
		return material.name() + (data == null? "" : "@" + data) + "*" + amount.toString();
	}
}
