package com.ModDamage.EventInfo;

public class DataRef<T>
{
	public final Class<? extends T> cls;
	public final String name;
	public final int index;
	
	public DataRef(Class<? extends T> cls, String name, int index)
	{
		this.cls = cls;
		this.name = name;
		this.index = index;
	}
	
	public T get(EventData data)
	{
		return data.get(cls, index);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
