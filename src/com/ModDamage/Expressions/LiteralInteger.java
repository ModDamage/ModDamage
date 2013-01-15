package com.ModDamage.Expressions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.BaseDataParser;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class LiteralInteger implements IDataProvider<Integer>
{
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("[0-9]+"), new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					return sm.acceptIf(new LiteralInteger(Integer.parseInt(m.group(0))));
				}
			});
	}
	
	int value;
	
	public LiteralInteger(int value)
	{
		this.value = value;
	}
	
	@Override
	public Integer get(EventData data)
	{
		return value;
	}
	
	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	@Override
	public String toString()
	{
		return ""+value;
	}
}
