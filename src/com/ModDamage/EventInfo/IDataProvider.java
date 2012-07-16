package com.ModDamage.EventInfo;

import com.ModDamage.Backend.BailException;

public interface IDataProvider<T>
{
	public T get(EventData data) throws BailException;
	public abstract Class<T> provides();
}
