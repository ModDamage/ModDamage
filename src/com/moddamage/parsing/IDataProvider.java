package com.moddamage.parsing;

import com.moddamage.backend.BailException;
import com.moddamage.backend.Nullable;
import com.moddamage.eventinfo.EventData;

public interface IDataProvider<T>
{
	@Nullable
    public T get(EventData data) throws BailException;
	public abstract Class<? extends T> provides();
}
