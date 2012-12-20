package com.ModDamage.Parsing.Property;


import com.ModDamage.Parsing.DataProvider;

public final class Properties {
    private Properties() {}

    public static void register(String name, Class<?> cls, String getterMethodName)
    {
    	register(ReflectedProperty.get(name, cls, getterMethodName));
    }

    public static void register(String name, Class<?> cls, String getterMethodName, String setterMethodName)
    {
        register(ReflectedSettableProperty.get(name, cls, getterMethodName, setterMethodName));
    }

    public static <T, S> void register(Property<T, S> property) {
        DataProvider.register(property.provides, property.startsWith, property.pattern, property.parser());
    }


    public static Class<?> toObjectClass(Class<?> cls) {
        if (!cls.isPrimitive()) return cls;
        if (cls == boolean.class)	return Boolean.class;
        if (cls == byte.class)		return Byte.class;
        if (cls == char.class)		return Character.class;
        if (cls == short.class)		return Short.class;
        if (cls == int.class)		return Integer.class;
        if (cls == long.class)		return Long.class;
        if (cls == float.class)		return Float.class;
        if (cls == double.class)	return Double.class;
        return cls;
    }
}
