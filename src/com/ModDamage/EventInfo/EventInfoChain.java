package com.ModDamage.EventInfo;

import java.util.HashSet;
import java.util.Set;

public class EventInfoChain extends EventInfo
{
	final EventInfo first, second;
	
	
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
	public int getIndex(Class<?> cls, String name) { return getIndex(cls, name, true); }

	@Override
	public int getIndex(Class<?> cls, String name, boolean complain)
	{
		int index = second.getIndex(cls, name, false);
		if (index != -1)
			return index + first.getSize();
		return first.getIndex(cls, name, complain);
	}


	public Set<String> getAll(Class<?> cls)
	{
		Set<String> names = new HashSet<String>();
		names.addAll(first.getAll(cls));
		names.addAll(second.getAll(cls));
		return names;
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
