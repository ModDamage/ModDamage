package com.ModDamage.EventInfo;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Nullable;

public interface IDataProvider<T>
{
	@Nullable
    public T get(EventData data) throws BailException;
	public abstract Class<? extends T> provides();
}
