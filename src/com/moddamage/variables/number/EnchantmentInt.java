package com.moddamage.variables.number;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;

import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;
import com.moddamage.LogUtil;
import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.backend.EnchantmentsRef;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.expressions.SettableIntegerExp;

public class EnchantmentInt extends SettableIntegerExp<EnchantmentsRef>
{
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("enchant(?:ment)?_?level_(\\w+)", Pattern.CASE_INSENSITIVE), new BaseDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
						IDataProvider<EnchantmentsRef> enchantmentsDP = info.get(EnchantmentsRef.class, "enchantments", false);
						if (enchantmentsDP == null)
						{
							LogUtil.error("You can only use '"+m.group()+"' inside of an Enchant or PrepareEnchant event");
							return null;
						}
						
						Enchantment enchantment = Enchantment.getByName(m.group(1).toUpperCase());
						if (enchantment == null)
						{
							LogUtil.error("Unknown enchantment named \"" + m.group(1) + "\"");
							return null;
						}
						return sm.acceptIf(new EnchantmentInt(
								enchantmentsDP,
								enchantment));
					}
				});
	}
	
	private final Enchantment enchantment;
	
	EnchantmentInt(IDataProvider<EnchantmentsRef> enchantmentsDP, Enchantment enchantment)
	{
		super(EnchantmentsRef.class, enchantmentsDP);
		this.enchantment = enchantment;
	}
	
	@Override
	public Integer myGet(EnchantmentsRef enchantments, EventData data) throws BailException
	{
		Integer level = enchantments.map.get(enchantment);
		return level == null? 0 : level;
	}
	
	@Override
	public void mySet(EnchantmentsRef enchantments, EventData data, Integer value)
	{
		// lock the enchantment value inside the acceptable range
		value = Math.min(Math.max(enchantment.getStartLevel(), value), enchantment.getMaxLevel());
		
		enchantments.map.put(enchantment, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return "enchantmentlevel_" + enchantment.getName();
	}
}