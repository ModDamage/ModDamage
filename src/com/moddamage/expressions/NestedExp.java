package com.moddamage.expressions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.StringMatcher;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.BaseDataParser;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

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
	public Class<? extends T> provides() { return inner.provides(); }
	
	@Override
	public String toString()
	{
		return "("+inner+")";
	}
}
