package com.moddamage.parsing;

import java.util.regex.Matcher;

import com.moddamage.StringMatcher;
import com.moddamage.eventinfo.EventInfo;

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