package com.ModDamage.EventInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.misc.Multimap;

public class EventInfoChain extends EventInfo
{
	private final EventInfo first, second;
	
	
	public EventInfoChain(EventInfo first, EventInfo second)
	{
		this.first = first;
		this.second = second;
	}

	
	@Override
	public int getSize()
	{
		return first.getSize() + second.getSize();
	}

	@Override
	protected int myGetIndex(Class<?> cls, String name)
	{
		int index = second.myGetIndex(cls, name);
		if (index != -1)
			return index + first.getSize();
		return first.myGetIndex(cls, name);
	}
	
	@Override
	public Class<?> getClass(int index)
	{
		if (index >= first.getSize())
			return second.getClass(index - first.getSize());
		return first.getClass(index);
	}


	public Multimap<String, Class<?>> getAllNames()
	{
		Multimap<String, Class<?>> allNames = first.getAllNames();
		allNames.putAll(second.getAllNames());
		return allNames;
	}
	public Set<String> getAllNames(Class<?> cls)
	{
		Set<String> names = first.getAllNames(cls);
		names.addAll(second.getAllNames(cls));
		return names;
	}
	
	public Set<String> getAllNames(Class<?> cls, String name)
	{
		Set<String> names = first.getAllNames(cls, name);
		if (names != null) return new HashSet<String>(names);
		names = second.getAllNames(cls, name);
		if (names != null) return new HashSet<String>(names);
		return null;
	}
	
	@Override
	protected void fillNamesLists(List<List<String>> namesLists, int offset)
	{
		first.fillNamesLists(namesLists, offset);
		second.fillNamesLists(namesLists, offset + first.getSize());
	}
	
	@Override
	public IDataProvider<Number> getLocal(String name)
	{
		return first.getLocal(name);
	}
	
	@Override
	public int getNumLocals()
	{
		return first.getNumLocals();
	}
	
	@Override
	protected void verify(EventData data)
	{
		// verify data.parent.info == first if EventData stored its EventInfo
		second.verify(data);
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + first.hashCode();
		result = prime * result + second.hashCode();
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		EventInfoChain other = (EventInfoChain) obj;
		if (!first.equals(other.first)) return false;
		if (!second.equals(other.second)) return false;
		return true;
	}

}
