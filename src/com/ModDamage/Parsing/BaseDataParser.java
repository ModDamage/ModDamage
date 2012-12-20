package com.ModDamage.Parsing;

import java.util.regex.Matcher;

import com.ModDamage.StringMatcher;
import com.ModDamage.EventInfo.EventInfo;

public abstract class BaseDataParser<T> implements IDataParser<T, Object>
{
	@Override
	public final IDataProvider<T> parse(EventInfo info, IDataProvider<Object> nullDP, Matcher m, StringMatcher sm)
	{
		if (nullDP != null) return null;
		return parse(info, m, sm);
	}
	
	public abstract IDataProvider<T> parse(EventInfo info, Matcher m, StringMatcher sm);
}