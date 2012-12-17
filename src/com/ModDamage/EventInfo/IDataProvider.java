package com.ModDamage.EventInfo;

import com.ModDamage.Backend.BailException;
import com.sun.istack.internal.Nullable;

public interface IDataProvider<T>
{
	@Nullable
    public T get(EventData data) throws BailException;
	public abstract Class<T> provides();
}
