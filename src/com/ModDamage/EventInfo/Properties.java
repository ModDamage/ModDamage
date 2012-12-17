package com.ModDamage.EventInfo;


import java.lang.reflect.Method;

import com.ModDamage.Backend.BailException;

public final class Properties {
    private Properties() {}

    public static void register(String name, Class<?> cls, String getterMethodName)
    {
        Method getter;
        try
        {
            getter = cls.getMethod(getterMethodName);
        }
        catch (Exception e)
        {
            System.err.println("Could not load method " + cls.getSimpleName() + "." + getterMethodName + ": " + e);
            return;
        }

        register(name, getter);
    }

    public static void register(String name, Class<?> cls, String getterMethodName, String setterMethodName)
    {
        Method getter, setter;
        try
        {
            getter = cls.getMethod(getterMethodName);
        }
        catch (Exception e)
        {
            System.err.println("Could not load method " + cls.getSimpleName() + "." + getterMethodName + ": " + e);
            return;
        }

        try
        {
            setter = cls.getMethod(setterMethodName, getter.getReturnType());
        }
        catch (Exception e)
        {
            System.err.println("Could not load method " + cls.getSimpleName() + "." + setterMethodName + ": " + e);
            return;
        }

        register(name, getter, setter);
    }

    public static void register(final String name, final Method getter)
    {
        getter.setAccessible(true);

        Class<?> provides = mapClass(getter.getReturnType());
        Class<?> wants = mapClass(getter.getDeclaringClass());

        register_helper(provides, wants, name, getter);
    }



    public static void register(final String name, final Method getter, final Method setter)
    {
        getter.setAccessible(true);
        setter.setAccessible(true);

        Class<?> provides = mapClass(getter.getReturnType());
        Class<?> wants = mapClass(getter.getDeclaringClass());

        register_helper(provides, wants, name, getter, setter);
    }

    // avoid rawtypes warning
    private static <T, S> void register_helper(final Class<T> provides, final Class<S> wants, final String name, final Method getter)
    {
        final Property<T, S> property = new Property<T, S>(name, provides, wants) {
            boolean once = false;

            @SuppressWarnings("unchecked")
            public T get(S obj, EventData data) {
                try {
                    return (T) getter.invoke(obj);
                }
                catch (Exception e) {
                    if (!once) { e.printStackTrace(); once = true; }
                }
                return null;
            }

            public String toString(IDataProvider<S> startDP) {
                return startDP + "_" + name;
            }
        };

        register(property);
    }

    private static <T, S> void register_helper(final Class<T> provides, final Class<S> wants, final String name, final Method getter, final Method setter)
    {
        final Property<T, S> property = new SettableProperty<T, S>(name, provides, wants) {
            boolean once = false;

            @SuppressWarnings("unchecked")
            public T get(S obj, EventData data) {
                try {
                    return (T) getter.invoke(obj);
                }
                catch (Exception e) {
                    if (!once) { e.printStackTrace(); once = true; }
                }
                return null;
            }

            public void set(S obj, EventData data, T value) throws BailException {
                try {
                    setter.invoke(obj, value);
                }
                catch (Exception e) {
                    if (!once) { e.printStackTrace(); once = true; }
                }
            }

            public String toString(IDataProvider<S> startDP) {
                return startDP + "_" + name;
            }
        };

        register(property);
    }


    public static <T, S> void register(Property<T, S> property) {
        DataProvider.register(property.provides, property.startsWith, property.pattern, property.parser());
    }


    public static Class<?> mapClass(Class<?> cls) {
        if (!cls.isPrimitive()) return cls;
        if (cls == int.class || cls == byte.class || cls == short.class || cls == long.class)
            return Integer.class;
        if (cls == float.class)
            return Float.class;
        if (cls == double.class)
            return Double.class;
        return cls;
    }
}
