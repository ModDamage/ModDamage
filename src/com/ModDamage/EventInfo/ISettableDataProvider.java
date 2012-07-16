package com.ModDamage.EventInfo;

import com.ModDamage.Backend.BailException;

public interface ISettableDataProvider<T> extends IDataProvider<T>
{
	public void set(EventData data, T value) throws BailException;
	public boolean isSettable();

}