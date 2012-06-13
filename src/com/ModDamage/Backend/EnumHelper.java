package com.ModDamage.Backend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class EnumHelper
{
	private EnumHelper() {}
	
	private static Map<Class<?>, Map<String, Enum<?>>> enumMap = new HashMap<Class<?>, Map<String, Enum<?>>>();
	
	public static Map<String, Enum<?>> getTypeMapForEnum(Class<?> cls)
	{
		if (enumMap.containsKey(cls)) return enumMap.get(cls);
		
		Map<String, Enum<?>> map = new HashMap<String, Enum<?>>();
		
		Method valuesMethod;
		@SuppressWarnings("rawtypes")
		Enum[] values;
		
		try
		{
			valuesMethod = cls.getMethod("values");
			values = (Enum[]) valuesMethod.invoke(null);
			
			for (Enum<?> value : values)
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
