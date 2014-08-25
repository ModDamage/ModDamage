package com.ModDamage.Parsing.Property;

import java.lang.reflect.Method;

import com.ModDamage.EventInfo.EventData;

public class ReflectedProperty<T, S> extends Property<T, S>
{
	Method getter;
	boolean once = false;

	@SuppressWarnings("unchecked")
	public static <T, S> ReflectedProperty<T, S> get(String name, Class<S> start, String getterMethodName)
	{
        try
        {
            Method getter = start.getMethod(getterMethodName);
            return new ReflectedProperty<T, S>(name, (Class<T>) Properties.toObjectClass(getter.getReturnType()), start, getter);
        }
        catch (Exception e)
        {
            System.err.println("Could not load method " + start.getSimpleName() + "." + getterMethodName + ": " + e);
            return null;
        }
	}
	
	public ReflectedProperty(String name, Class<T> provides, Class<S> start, Method getter)
	{
        super(name, provides, start);
        
        this.getter = getter;
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
        	if (!once) {
            	once = true;
            	
            	if (e instanceof IllegalArgumentException)
            		System.err.println("_"+ name
            				+" tried to call "+ getter
            				+" on a "+ (start == null? "null" : start.getClass().getSimpleName()));
            	else
            		e.printStackTrace();
            }
        }
		return null;
	}

}
