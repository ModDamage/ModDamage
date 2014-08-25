package com.moddamage.variables.number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;

import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ItemHolder;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.SettableIntegerExp;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class ItemEnchantmentInt extends SettableIntegerExp<ItemHolder>
{
	public static void register()
	{
		DataProvider.register(Integer.class, ItemHolder.class, Pattern.compile("_enchant(?:ment)?_?level_(\\w+)", Pattern.CASE_INSENSITIVE), new IDataParser<Integer, ItemHolder>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<ItemHolder> itemDP, Matcher m, StringMatcher sm)
					{
						Enchantment enchantment = Enchantment.getByName(m.group(1).toUpperCase());
						if (enchantment == null)
						{
							LogUtil.error("Unknown enchantment named \"" + m.group(1) + "\"");
							return null;
						}
						return sm.acceptIf(new ItemEnchantmentInt(
								itemDP,
								enchantment));
					}
				});
	}
	
	private final Enchantment enchantment;
	
	ItemEnchantmentInt(IDataProvider<ItemHolder> itemDP, Enchantment enchantment)
	{
		super(ItemHolder.class, itemDP);
		this.enchantment = enchantment;
	}
	
	@Override
	public Integer myGet(ItemHolder item, EventData data) throws BailException
	{
		return item.getEnchantmentLevel(enchantment);
	}
	
	@Override
	public void mySet(ItemHolder item, EventData data, Integer value)
	{
		item.setEnchantmentLevel(enchantment, value);
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