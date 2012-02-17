package com.ModDamage.EventInfo;

import java.util.HashSet;
import java.util.Set;

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
			return second.getClass(index);
		return first.getClass(index);
	}


	public Set<String> getAllNames(Class<?> cls)
	{
		Set<String> names = new HashSet<String>();
		Set<String> tnames = first.getAllNames(cls);
		if (tnames != null) names.addAll(tnames);
		tnames = second.getAllNames(cls);
		if (tnames != null) names.addAll(tnames);
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
