package com.ModDamage.Parsing;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;

public interface ISettableDataProvider<T> extends IDataProvider<T>
{
	public void set(EventData data, T value) throws BailException;
	public boolean isSettable();

}