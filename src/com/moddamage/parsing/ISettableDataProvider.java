package com.moddamage.parsing;

import com.moddamage.backend.BailException;
import com.moddamage.backend.Nullable;
import com.moddamage.eventinfo.EventData;

public interface ISettableDataProvider<T> extends IDataProvider<T>
{
	public void set(EventData data, @Nullable T value) throws BailException;
	public boolean isSettable();

}