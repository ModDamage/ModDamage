package com.ModDamage.Backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.ModDamage.Matchables.Matchable;

public final class EnumHelper
{
	private EnumHelper() {}
	
	private static Map<Class<?>, Map<String, Matchable<?>>> enumMap = new HashMap<Class<?>, Map<String,Matchable<?>>>();
	
	public static Map<String, Matchable<?>> getTypeMapForEnum(Class<?> cls)
	{
		if (enumMap.containsKey(cls)) return enumMap.get(cls);
		
		Map<String, Matchable<?>> map = new HashMap<String, Matchable<?>>();
		
		Method valuesMethod;
		@SuppressWarnings("rawtypes")
		Matchable[] values;
		
		try
		{
			valuesMethod = cls.getMethod("values");
			values = (Matchable[]) valuesMethod.invoke(null);
			
			for (Matchable<?> value : values)
				map.put(value.name(), value);
		}
		catch (SecurityException e) { e.printStackTrace(); }
		catch (NoSuchMethodException e) { e.printStackTrace(); }
		catch (IllegalArgumentException e) { e.printStackTrace(); }
		catch (IllegalAccessException e) { e.printStackTrace(); }
		catch (InvocationTargetException e) { e.printStackTrace(); }
		
		
		enumMap.put(cls, map);
		return map;
	}
}
