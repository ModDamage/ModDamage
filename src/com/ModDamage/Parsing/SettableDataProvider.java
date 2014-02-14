package com.ModDamage.Parsing;

import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public abstract class SettableDataProvider<T, S> extends DataProvider<T, S> implements ISettableDataProvider<T>
{
	protected SettableDataProvider(Class<S> wantStart, IDataProvider<S> startDP)
	{
		super(wantStart, startDP);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void set(EventData data, T value) throws BailException
	{
		Object ostart = startDP.get(data);
		if (ostart != null && wantStart.isInstance(ostart))
			set((S) ostart, data, value);
	}
	public abstract void set(S start, EventData data, T value) throws BailException;
	
	
	public static <T> ISettableDataProvider<T> parse(EventInfo info, Class<T> want, String s)
	{
		return parse(info, want, s, true, true);
	}
	
	public static <T> ISettableDataProvider<T> parse(EventInfo info, Class<T> want, String s, boolean finish, boolean complain)
	{
		return parse(info, want, new StringMatcher(s), finish, complain, null);
	}

	public static <T> ISettableDataProvider<T> parse(EventInfo info, Class<T> want, StringMatcher sm)
	{
		return parse(info, want, sm, false, true, null);
	}
	
	public static <T> ISettableDataProvider<T> parse(EventInfo info, Class<T> want, StringMatcher sm, boolean finish, boolean complain, Pattern endPattern)
	{
		IDataProvider<T> dp = DataProvider.parse(info, want, sm, finish, complain, endPattern);
		if (dp == null) return null;
		
		if (!(dp instanceof ISettableDataProvider) || !((ISettableDataProvider<?>)dp).isSettable())
		{
			LogUtil.error(dp+" is not settable");
			return null;
		}
		return (ISettableDataProvider<T>) dp;
	}
}
