package com.moddamage.parsing;

import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;

public class CastDataProvider<T> implements IDataProvider<T>
{
	private final IDataProvider<?> inner;
	private final Class<T> want;
	
	public CastDataProvider(IDataProvider<?> inner, Class<T> want)
	{
		this.inner = inner;
		this.want = want;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public T get(EventData data) throws BailException
	{
		Object obj = inner.get(data);
		if (want.isInstance(obj)) return (T) obj;
		return null;
	}

	@Override
	public Class<T> provides()
	{
		return want;
	}
	
	@Override
	public String toString()
	{
        return inner.toString();

		//return "("+inner.provides().getSimpleName() + "->" + want.getSimpleName() + ")" + inner.toString();
	}
}