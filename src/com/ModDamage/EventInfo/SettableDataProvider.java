package com.ModDamage.EventInfo;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;

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
		return parse(info, want, new StringMatcher(s));
	}
	
	public static <T> ISettableDataProvider<T> parse(EventInfo info, Class<T> want, StringMatcher sm)
	{
		IDataProvider<T> dp = DataProvider.parse(info, want, sm);
		if (dp == null) return null;
		
		if (!(dp instanceof ISettableDataProvider) || !((ISettableDataProvider<?>)dp).isSettable())
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, dp+" is not settable");
			return null;
		}
		return (ISettableDataProvider<T>) dp;
	}
}
