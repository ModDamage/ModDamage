package com.ModDamage.EventInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SimpleEventInfo extends EventInfo
{
	private final Map<Class<?>, Map<String, Integer>> map;
	private final List<Class<?>> classes;
	private final int size;
	private final int hashCode;
	
	public SimpleEventInfo(Object... objs)
	{
		this.classes = new ArrayList<Class<?>>();
		Map<Class<?>, Map<String, Integer>> mmap = new HashMap<Class<?>, Map<String,Integer>>();
		int objIndex = 0;
		int i = 0;
		List<Class<?>> classes = new ArrayList<Class<?>>();
		List<String> names = new ArrayList<String>();
		
		hashCode = objs.hashCode();
		
		while (i < objs.length) {
			Object obj = objs[i++];
			if (obj instanceof Class<?>)
			{
				if (!names.isEmpty())
				{				
					objIndex = addItems(classes, names, mmap, objIndex);
					
					classes.clear();
					names.clear();
				}

				classes.add((Class<?>) obj);
				this.classes.add((Class<?>) obj);
			}
			else // will get a bad cast error from below if it isn't a String, no need to check
			{
				names.add((String) obj);
			}
		}
		size = addItems(classes, names, mmap, objIndex);
		
		map = mmap;
	}
	
	// I really wish Java had inner methods...
	private int addItems(List<Class<?>> classes, List<String> names, Map<Class<?>, Map<String, Integer>> mmap, int objIndex)
	{
		for (Class<?> cls : classes)
		{
			Map<String, Integer> currMap;
			if (mmap.containsKey(cls))
				currMap = mmap.get(cls);
			else
				currMap = new HashMap<String, Integer>();
			
			for (String name : names)
				currMap.put(name, objIndex);
			
			mmap.put(cls, currMap);
			objIndex ++;
		}
		return objIndex;
	}

	@Override
	public int getSize()
	{
		return size;
	}

	@Override
	protected int myGetIndex(Class<?> cls, String name)
	{
		for (Entry<Class<?>, Map<String, Integer>> entry : map.entrySet())
		{
			if (cls.isAssignableFrom(entry.getKey()))
			{
				Integer integer = entry.getValue().get(name);
				if (integer != null)
					return integer;
			}
		}
		
		return -1;
	}
	
	@Override
	protected Class<?> getClass(int index)
	{
		if (index > classes.size()) return null;
		return classes.get(index);
	}

	@Override
	public Set<String> getAllNames(Class<?> cls)
	{
		Map<String, Integer> argMap = map.get(cls); if (argMap == null) return null;
		return argMap.keySet();
	}

	@Override
	public Set<String> getAllNames(Class<?> cls, String name)
	{
		int index = getIndex(cls, name, false); if (index == -1) return null;
		Map<String, Integer> argMap = map.get(cls); // no need to check for null, index will be -1 in that case
		Set<String> names = new HashSet<String>();
		
		for (Entry<String, Integer> entry : argMap.entrySet())
			if (entry.getValue() == index)
				names.add(entry.getKey());
		
		return names;
	}

	@Override
	protected void verify(EventData data)
	{
		for (Entry<Class<?>, Map<String, Integer>> entry : map.entrySet())
		{
			Class<?> cls = entry.getKey();
			for (Entry<String, Integer> entry2 : entry.getValue().entrySet())
			{
				Object obj = data.get(data.start + entry2.getValue());
				if (obj != null && !cls.isInstance(obj))
					throw new VerificationException("Failed to verify: " + obj.getClass().getName() + " should have been " + cls.getName());
			}
		}
	}

	@Override
	public int hashCode()
	{
		return hashCode;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleEventInfo other = (SimpleEventInfo) obj;
		if (!map.equals(other.map)) return false;
		return true;
	}
}
