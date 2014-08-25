package com.moddamage.eventinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.moddamage.parsing.IDataProvider;
import com.moddamage.parsing.ISettableDataProvider;
import com.moddamage.misc.Multimap;

public class SimpleEventInfo extends EventInfo
{
	private final Map<Class<?>, Map<String, Integer>> map;
	private final List<Class<?>> classes;
	private final int size;
	private final int hashCode;
	
	private final Map<String, Integer> localMap = new HashMap<String, Integer>();
	private int numLocals;

	public SimpleEventInfo(Object... objs)
	{
		this(objs, false);
	}
	public SimpleEventInfo(Object[] objs, boolean dummy)
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
					if(classes.isEmpty()) throw new Error("CLASSES IS EMPTY");
					objIndex = addItems(classes, names, mmap, objIndex);
					
					classes.clear();
					names.clear();
				}

				classes.add((Class<?>) obj);
				this.classes.add((Class<?>) obj);
			}
			else // will get a bad cast error from below if it isn't a String, no need to check
			{
				if(classes.isEmpty()) throw new Error("CLASSES IS EMPTY");
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
			if (cls.isAssignableFrom(entry.getKey()) || entry.getKey().isAssignableFrom(cls))
			{
				Integer integer = entry.getValue().get(name);
				if (integer != null)
					return integer;
			}
		}
		
		return -1;
	}
	
	@Override
	public Class<?> getClass(int index)
	{
		if (index > classes.size()) return null;
		return classes.get(index);
	}

	@Override
	public Multimap<String, Class<?>> getAllNames()
	{
		Multimap<String, Class<?>> allNames = new Multimap<String, Class<?>>();
		for (Entry<Class<?>, Map<String, Integer>> entry : map.entrySet())
		{
			for (String name : entry.getValue().keySet())
				if (!name.startsWith("-"))
					allNames.put(name, entry.getKey());
		}
		return allNames;
	}
	
	@Override
	protected void fillNamesLists(List<List<String>> namesLists, int offset)
	{
		for (Entry<Class<?>, Map<String, Integer>> entry : map.entrySet())
		{
			for (Entry<String, Integer> mapEntry : entry.getValue().entrySet())
				if (!mapEntry.getKey().startsWith("-"))
					namesLists.get(mapEntry.getValue()).add(mapEntry.getKey());
		}
	}

	@Override
	public Set<String> getAllNames(Class<?> cls)
	{
		Set<String> names = new HashSet<String>();
		for (Entry<Class<?>, Map<String, Integer>> entry : map.entrySet())
		{
			if (cls.isAssignableFrom(entry.getKey()) || entry.getKey().isAssignableFrom(cls))
			{
				for (String name : entry.getValue().keySet())
					if (!name.startsWith("-"))
						names.add(name);
			}
		}
		return names;
	}

	@Override
	public Set<String> getAllNames(Class<?> cls, String name)
	{
		Set<String> names = new HashSet<String>();
		int index = getIndex(cls, name, false); if (index == -1) return names;
		Map<String, Integer> argMap = map.get(cls); // no need to check for null, index will be -1 in that case
		
		for (Entry<String, Integer> entry : argMap.entrySet())
			if (entry.getValue().equals(index) && !entry.getKey().startsWith("-"))
				names.add(entry.getKey());
		
		return names;
	}
	
	@Override
	public IDataProvider<Number> getLocal(final String name)
	{
		Integer i = localMap.get(name);
		if (i == null) {
			i = numLocals;
			localMap.put(name, numLocals++);
		}
		final int localIndex = i;
		
		return new ISettableDataProvider<Number>() {
				public Number get(EventData data) { return data.getLocal(localIndex); }
				public void set(EventData data, Number value) { data.setLocal(localIndex, value); }
				
				public Class<Number> provides() { return Number.class; }
				public boolean isSettable() { return true; }
				
				public String toString() { return "$" + name; }
			};
	}
	
	@Override
	public int getNumLocals()
	{
		return numLocals;
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
