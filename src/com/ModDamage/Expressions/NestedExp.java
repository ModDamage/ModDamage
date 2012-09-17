package com.ModDamage.Expressions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.DataProvider.BaseDataParser;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class NestedExp<T> implements IDataProvider<T>
{
	public static final Pattern openParen = Pattern.compile("\\s*\\(\\s*");
	public static final Pattern closeParen = Pattern.compile("\\s*\\)\\s*");
	
	public static void register()
	{
		DataProvider.register(Object.class, openParen, new BaseDataParser<Object>()
			{
				@Override
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public IDataProvider<Object> parse(EventInfo info, Matcher m, StringMatcher sm)
				{
					IDataProvider<?> nestedDP;
					nestedDP = DataProvider.parse(info, null, sm.spawn(), false, true, closeParen);
					
					if (nestedDP == null || !sm.matchesFront(closeParen)) return null;
					
					sm.accept();
					return (IDataProvider<Object>) new NestedExp(nestedDP);
				}
			});
	}
	
	IDataProvider<T> inner;
	
	public NestedExp(IDataProvider<T> inner)
	{
		this.inner = inner;
	}
	
	@Override
	public T get(EventData data) throws BailException
	{
		return inner.get(data);
	}
	
	@Override
	public Class<T> provides() { return inner.provides(); }
	
	@Override
	public String toString()
	{
		return "("+inner+")";
	}
}
