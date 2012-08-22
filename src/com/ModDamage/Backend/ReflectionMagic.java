package com.ModDamage.Backend;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.gen.ParserClassVisitor;
import com.ModDamage.Backend.gen.ParserSpec;
import com.ModDamage.Backend.gen.ProviderClassVisitor;
import com.ModDamage.Backend.gen.RegisterClassVisitor;
import com.ModDamage.EventInfo.Var;

public class ReflectionMagic
{
	private static ClassLoader cl;
	//private static MDMagicClassLoader mdmcl;
	
	public static void clear()
	{
		cl = ModDamage.getPluginConfiguration().plugin.getClass().getClassLoader();
		//mdmcl = new MDMagicClassLoader(cl);
		//cl = new MDMagicClassLoader(ReflectionMagic.class.getClassLoader());
	}
	
	public static void registerClass(String name, byte[] data)
	{
		System.out.println("registerClass: "+name);
		//mdmcl.addClassCode(name, data);
		java.lang.reflect.Method m;
		try
		{
			m = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
		}
		catch (SecurityException e)
		{
			e.printStackTrace(); return;
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace(); return;
		}
		
		m.setAccessible(true);
		try
		{
			m.invoke(cl, name, data, 0, data.length);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}
	
	private static File logDir = null;
	public static void setLogDir(File dir)
	{
		logDir = dir;
		logDir.mkdirs();
	}
	
	public static void register(Class<?> cls)
	{
		List<ParserSpec> parsers = new ArrayList<ParserSpec>(10);

		for (java.lang.reflect.Method method : cls.getDeclaredMethods())
		{
			if (method.isAnnotationPresent(Var.class))
			{
				Var v = method.getAnnotation(Var.class);

				Class<?> provides = method.getReturnType();
				if (!Object.class.isAssignableFrom(provides))
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad provides: "+provides+ " in method " + method);
					continue;
				}
				Class<?>[] params = method.getParameterTypes();
				Class<?> startsWith = null;
				if (!v.start())
				{
					startsWith = params[0];
					if (!Object.class.isAssignableFrom(startsWith))
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad startsWith: "+provides);
						continue;
					}
				}
				String pattern = v.pattern();
				if (pattern.equals("")) pattern = "_" + method.getName();

				setupNames(cls, method.getName(), provides, startsWith);
				Type providesType = Type.getType(provides);
				Type startsWithType = Type.getType(startsWith);
				ParserSpec pspec = ParserClassVisitor.createParserClass(parserIName, providesType, startsWithType, pattern);
				if (pspec != null) parsers.add(pspec);
				ProviderClassVisitor.createProviderClass(Type.getType(cls), Method.getMethod(method), providesType, startsWithType, pattern);
			}
		}
		
		registerClassName = parentName + "$register";

		RegisterClassVisitor.createRegisterClass(parsers);
		
		// classes have been created, time to call the magic register function!
		try
		{
			Class<?> registerCls = cl.loadClass(registerClassName);
			java.lang.reflect.Method m = registerCls.getMethod("register");
			
			m.setAccessible(true);
			
			m.invoke(null);
		}
		catch (Exception e) {
			System.err.println(e); }
	}
	
	public static String parentIName = null;
	public static String parentName = null;
	
	public static String registerIName = null;
	
	public static String parserIName = null;
	public static String parserDesc = null;

	public static String providerIName = null;
	public static String providerDesc = null;
	
	public static String registerClassName = null;
	public static String parserClassName = null;
	public static String providerClassName = null;

	private static void setupNames(Class<?> cls, String name, Class<?> provides, Class<?> startsWith)
	{
		parentIName = Type.getInternalName(cls);
		String baseIName = parentIName + "$" + provides.getSimpleName() + "_" + name;
		parserIName = baseIName + "$parser";
		providerIName = baseIName + "$provider";
		parserDesc = "L" + parserIName + ";";
		providerDesc = "L" + providerIName + ";";
		
		parentName = cls.getName();
		String baseClassName = parentName + "$" + provides.getSimpleName() + "_" + name;
		parserClassName = baseClassName + "$parser";
		providerClassName = baseClassName + "$provider";
	}
	
}
