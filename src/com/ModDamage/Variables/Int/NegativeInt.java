package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.DataProvider.BaseDataParser;

public class NegativeInt implements IDataProvider<Integer>
{
	public static void register()
	{
		DataProvider.register(Integer.class, Pattern.compile("-"), new BaseDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
					{
						IDataProvider<Integer> integer = DataProvider.parse(info, Integer.class, sm.spawn());
						if (integer == null) return null;
						
						return sm.acceptIf(new NegativeInt(integer));
					}
				});
	}
	
	IDataProvider<Integer> integer;
	
	public NegativeInt(IDataProvider<Integer> integer)
	{
		this.integer = integer;
	}
	
	@Override
	public Integer get(EventData data) throws BailException
	{
		return -integer.get(data);
	}

	@Override
	public Class<Integer> provides() { return Integer.class; }

	@Override
	public String toString()
	{
		return "-"+integer;
	}
}
