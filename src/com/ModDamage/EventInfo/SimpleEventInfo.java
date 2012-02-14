package com.ModDamage.EventInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class SimpleEventInfo extends EventInfo
{
	private final Map<Class<?>, Map<String, Integer>> map;
	private final int size;
	private final int hashCode;
	
	public SimpleEventInfo(Object... objs)
	{
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

	public int getSize()
	{
		return size;
	}

	public int getIndex(Class<?> cls, String name) { return getIndex(cls, name, true); }
	public int getIndex(Class<?> cls, String name, boolean complain)
	{
		Map<String, Integer> argMap = map.get(cls);
		if (argMap != null) 
		{
			Integer integer = argMap.get(name);
			if (integer != null)
				return integer;
		}
		if (complain)
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown "+cls.getSimpleName()+" named '"+ name +"'");
		return -1;
	}
	
	public Set<String> getAll(Class<?> cls)
	{
		Map<String, Integer> argMap = map.get(cls);
		if (argMap == null) return null;
		return argMap.keySet();
	}
	
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
