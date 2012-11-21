package com.ModDamage;


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
}
