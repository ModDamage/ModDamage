package com.ModDamage.Parsing;

import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;

public class EventDataProvider<T> implements ISettableDataProvider<T>
{
	public final Class<T> givenCls;
	public final Class<? extends T> infoCls;
	public final String name;
	public final int index;
	
	public EventDataProvider(Class<T> givenCls, Class<? extends T> infoCls, String name, int index)
	{
		this.givenCls = givenCls;
		this.infoCls = infoCls;
		this.name = name;
		this.index = index;
	}
	
	@SuppressWarnings("unchecked")
	public T get(EventData data)
	{
		Object obj = data.get(infoCls, index);
		if (obj != null && givenCls.isInstance(obj)) return (T) obj;
		return null;
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public Class<? extends T> provides()
	{
		return infoCls;
	}

	@Override
	public void set(EventData data, T value) throws BailException
	{
		if (value != null && infoCls.isInstance(value))
			data.set(index, infoCls.cast(value));
		else
			ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG,
					"Could not set \"" + name + "\" of type " + infoCls.getSimpleName() + " to " + value + " of type "
							+ givenCls.getSimpleName());
	}

	@Override
	public boolean isSettable()
	{
		return true;
	}
}
