package com.ModDamage.EventInfo;

public class DataRef<T>
{
	public final Class<T> givenCls;
	public final Class<?> infoCls;
	public final String name;
	public final int index;
	
	public DataRef(Class<T> givenCls, Class<?> infoCls, String name, int index)
	{
		this.givenCls = givenCls;
		this.infoCls = infoCls;
		this.name = name;
		this.index = index;
	}
	
	@SuppressWarnings("unchecked")
	public T get(EventData data)
	{
		Object obj = data.get(infoCls, index);
		if (obj != null && givenCls.isInstance(obj)) return (T) obj;
		return null;
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
