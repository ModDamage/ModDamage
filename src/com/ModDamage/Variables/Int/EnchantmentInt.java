package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.enchantments.Enchantment;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EnchantmentsRef;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.SettableIntegerExp;

public class EnchantmentInt extends SettableIntegerExp<EnchantmentsRef>
{
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("enchant(?:ment)?_?level_(\\w+)", Pattern.CASE_INSENSITIVE), new BaseDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, Class<?> want, Matcher m, StringMatcher sm)
					{
						IDataProvider<EnchantmentsRef> enchantmentsDP = info.get(EnchantmentsRef.class, "enchantments");
						
						Enchantment enchantment = Enchantment.getByName(m.group(1).toUpperCase());
						if (enchantment == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown enchantment named \"" + m.group(1) + "\"");
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