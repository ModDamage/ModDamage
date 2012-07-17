package com.ModDamage.Variables.Int;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.BaseDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class NestedInt implements IDataProvider<Integer>
{
	public static final Pattern openParen = Pattern.compile("\\s*\\(\\s*");
	public static final Pattern closeParen = Pattern.compile("\\s*\\)\\s*");
	
	public static void register()
	{
		DataProvider.register(Integer.class, openParen, new BaseDataParser<Integer>()
			{
				@Override
				public IDataProvider<Integer> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					IDataProvider<Integer> intDP = DataProvider.parse(info, Integer.class, sm.spawn());
					
					if (!sm.matchesFront(closeParen)) return null;
					
					sm.accept();
					return new NestedInt(intDP);
				}
			});
	}
	
	IDataProvider<Integer> integer;
	
	public NestedInt(IDataProvider<Integer> integer)
	{
		this.integer = integer;
	}
	
	@Override
	public Integer get(EventData data) throws BailException
	{
		return integer.get(data);
	}
	
	@Override
	public Class<Integer> provides() { return Integer.class; }
	
	@Override
	public String toString()
	{
		return ""+integer;
	}
}
