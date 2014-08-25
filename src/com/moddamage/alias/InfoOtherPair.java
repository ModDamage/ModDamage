package com.moddamage.alias;

import com.moddamage.eventinfo.EventInfo;

class InfoOtherPair<T> {
	private final T alias;
	private final EventInfo info;
	
	public InfoOtherPair(T alias, EventInfo info)
	{
		this.alias = alias;
		this.info = info;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + alias.hashCode();
		result = prime * result + info.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		InfoOtherPair<?> other = (InfoOtherPair<?>) obj;
		if (!alias.equals(other.alias)) return false;
		if (!info.equals(other.info)) return false;
		return true;
	}
}