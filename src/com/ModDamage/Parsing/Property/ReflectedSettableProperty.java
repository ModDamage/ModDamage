package com.ModDamage.Parsing.Property;

import java.lang.reflect.Method;

import com.ModDamage.EventInfo.EventData;

public class ReflectedSettableProperty<T, S> extends SettableProperty<T, S>
{
	Method getter, setter;
	boolean once = false;

	@SuppressWarnings("unchecked")
	public static <T, S> ReflectedSettableProperty<T, S> get(String name, Class<S> start, String getterMethodName, String setterMethodName)
	{
		Method getter, setter;
        try
        {
            getter = start.getMethod(getterMethodName);
        }
        catch (Exception e)
        {
            System.err.println("Could not load method " + start.getSimpleName() + "." + getterMethodName + ": " + e);
            return null;
        }
        
        try
        {
            setter = start.getMethod(setterMethodName, getter.getReturnType());
        }
        catch (Exception e)
        {
            System.err.println("Could not load method " + start.getSimpleName() + "." + setterMethodName + ": " + e);
            return null;
        }
        
        
        return new ReflectedSettableProperty<T, S>(name, (Class<T>) getter.getReturnType(), start, getter, setter);
	}
	
	public ReflectedSettableProperty(String name, Class<T> provides, Class<S> start, Method getter, Method setter)
	{
        super(name, provides, start);

        this.getter = getter;
        this.setter = setter;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(S start, EventData data)
	{
		try
		{
			return (T) getter.invoke(start);
		}
        catch (Exception e) {
            if (!once) { e.printStackTrace(); once = true; }
        }
		return null;
	}

	@Override
	public void set(S start, EventData data, T value)
	{
		try
		{
			setter.invoke(start, value);
		}
        catch (Exception e) {
            if (!once) { e.printStackTrace(); once = true; }
        }
	}
}
