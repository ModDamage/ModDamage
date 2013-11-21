package com.ModDamage;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Utils
{
	private Utils() {}

	public static String joinBy(String sep, Iterable<?> objs)
	{
		boolean first = true;

		StringBuilder sb = new StringBuilder();
		for(Object obj : objs)
		{
			if (first) first = false;
			else sb.append(sep);
			sb.append(obj);
		}

		return sb.toString();
	}

	public static <T> String joinBy(String sep, T... objs)
	{
		if (objs.length == 0) return "";

		boolean first = true;

		StringBuilder sb = new StringBuilder();
		for(T obj : objs)
		{
			if (first) first = false;
			else sb.append(sep);
			sb.append(obj);
		}

		return sb.toString();
	}

	public static <T> String join(T... objs)
	{
		if (objs.length == 0) return "";

		StringBuilder sb = new StringBuilder();
		for(T obj : objs)
			sb.append(obj);

		return sb.toString();
	}

	public static String safeToString(Object obj)
	{
		try
		{
			return "'"+ obj.toString() +"'";
		}
		catch (Throwable t)
		{
			return "error!";
		}
	}

	public static <T> List<T> asList(Collection<T> coll)
	{
		if (coll instanceof List)
			return (List<T>)coll;
		else
			return new ArrayList<T>(coll);
	}

	public static boolean isFloating(Class<?> cls)
	{
		return cls == Float.class || cls == Double.class;
	}
	
	public static boolean isFloating(Number num)
	{
		return isFloating(num.getClass());
	}
	

	private static Map<Class<?>, Map<String, Enum<?>>> enumMap = new HashMap<Class<?>, Map<String, Enum<?>>>();

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> Map<String, T> getTypeMapForEnum(Class<T> cls, boolean uppercase)
	{
		if (enumMap.containsKey(cls)) return (Map<String, T>) (Object) enumMap.get(cls);
		
		Map<String, T> map = new HashMap<String, T>();
		
		Method valuesMethod;
		@SuppressWarnings("rawtypes")
		Enum[] values;
		
		try
		{
			valuesMethod = cls.getMethod("values");
			values = (Enum[]) valuesMethod.invoke(null);
			
			for (Enum<?> value : values)
				map.put(uppercase? value.name().toUpperCase() : value.name(), (T) value);
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchMethodException e) { e.printStackTrace(); }
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		catch (InvocationTargetException e) { e.printStackTrace(); }
		
		
		enumMap.put(cls, (Map<String, Enum<?>>) map);
		return map;
	}
}
