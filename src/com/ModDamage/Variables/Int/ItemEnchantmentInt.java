package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.SettableIntegerExp;

public class ItemEnchantmentInt extends SettableIntegerExp<ItemStack>
{
	public static void register()
	{
		DataProvider.register(Integer.class, ItemStack.class, Pattern.compile("_enchant(?:ment)?_?level_(\\w+)", Pattern.CASE_INSENSITIVE), new IDataParser<Integer, ItemStack>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<ItemStack> itemDP, Matcher m, StringMatcher sm)
					{
						Enchantment enchantment = Enchantment.getByName(m.group(1).toUpperCase());
						if (enchantment == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown enchantment named \"" + m.group(1) + "\"");
							return null;
						}
						return sm.acceptIf(new ItemEnchantmentInt(
								itemDP,
								enchantment));
					}
				});
	}
	
	private final Enchantment enchantment;
	
	ItemEnchantmentInt(IDataProvider<ItemStack> itemDP, Enchantment enchantment)
	{
		super(ItemStack.class, itemDP);
		this.enchantment = enchantment;
	}
	
	@Override
	public Integer myGet(ItemStack item, EventData data) throws BailException
	{
		return item.getEnchantmentLevel(enchantment);
	}
	
	@Override
	public void mySet(ItemStack item, EventData data, Integer value)
	{
		// lock the enchantment value inside the acceptable range
		value = Math.min(Math.max(enchantment.getStartLevel(), value), enchantment.getMaxLevel());
		
		item.addUnsafeEnchantment(enchantment, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return startDP + "_enchantmentlevel_" + enchantment.getName();
	}
}