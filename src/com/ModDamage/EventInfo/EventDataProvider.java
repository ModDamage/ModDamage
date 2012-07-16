package com.ModDamage.EventInfo;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;

class EventDataProvider<T> implements ISettableDataProvider<T>
{
	public final Class<T> givenCls;
	public final Class<?> infoCls;
	public final String name;
	public final int index;
	
	public EventDataProvider(Class<T> givenCls, Class<?> infoCls, String name, int index)
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
	public Class<T> provides()
	{
		return givenCls;
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
