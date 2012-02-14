package com.ModDamage.EventInfo;

public class DataRef<T>
{
	final Class<T> cls;
	final String name;
	final int index;
	
	public DataRef(Class<T> cls, String name, int index)
	{
		this.cls = cls;
		this.name = name;
		this.index = index;
	}
	
	public T get(EventData data)
	{
		return data.get(cls, index);
	}
	
	public void set(EventData data, T value)
	{
		data.set(cls, index, value);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
