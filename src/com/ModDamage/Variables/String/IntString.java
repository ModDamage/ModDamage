package com.ModDamage.Variables.String;

import java.util.regex.Matcher;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.StringExp;

public class IntString extends StringExp<Integer>
{
	public IntString(IDataProvider<?> intDP)
	{
		super(Integer.class, intDP);
	}
	
	@Override
	public String get(Integer integer, EventData data)
	{
		return integer.toString();
	}
	
	public static void register()
	{
		DataProvider.registerTransformer(String.class, Integer.class, new IDataParser<String>()
			{
				@Override
				public IDataProvider<String> parse(EventInfo info, IDataProvider<?> entityDP, Matcher m, StringMatcher sm)
				{
					return new IntString(entityDP);
				}
			});
	}

	@Override
	public String toString()
	{
		return startDP.toString();
	}
}
