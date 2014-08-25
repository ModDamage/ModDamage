package com.ModDamage.Parsing;

import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Nullable;
import com.ModDamage.EventInfo.EventData;

public interface IDataProvider<T>
{
	@Nullable
    public T get(EventData data) throws BailException;
	public abstract Class<? extends T> provides();
}
