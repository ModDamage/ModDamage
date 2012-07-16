package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.IDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Constant implements IDataProvider<Integer>
{
	public static void register()
	{
		DataProvider.register(Integer.class, null, Pattern.compile("[0-9]+"), new IDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, IDataProvider<?> start, Matcher m, StringMatcher sm)
				{
					return sm.acceptIf(new Constant(Integer.parseInt(m.group(0))));
				}
			});
	}
	
	int constant;
	
	public Constant(int constant)
	{
		this.constant = constant;
	}
	
	@Override
	public Integer get(EventData data)
	{
		return constant;
	}
	
	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	@Override
	public String toString()
	{
		return ""+constant;
	}
}
