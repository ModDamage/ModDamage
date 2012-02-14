package com.ModDamage.EventInfo;

import java.util.Set;

public abstract class EventInfo
{
	public abstract int getSize();
	
	
	public abstract int getIndex(Class<?> cls, String name);
	public abstract int getIndex(Class<?> cls, String name, boolean complain);
	
	public abstract Set<String> getAll(Class<?> cls);

	protected abstract void verify(EventData data);
	
	public abstract int hashCode();
	public abstract boolean equals(Object other);
	
	
	
	public <T> DataRef<T> get(Class<T> cls, String name) { return get(cls, name, true); }
	public <T> DataRef<T> get(Class<T> cls, String name, boolean complain)
	{
		int index = getIndex(cls, name, complain);
		if (index == -1) return null;
		return new DataRef<T>(cls, name, index);
	}
	
	public EventData makeData(Object... objs) { return makeChainedData(null, objs); }
	public EventData makeChainedData(EventData parent, Object... objs)
	{
		EventData data = new EventData(parent, objs);
		verify(data);
		return data;
	}
	
	public EventInfo chain(EventInfo second)
	{
		return new EventInfoChain(this, second);
	}
	
	
	class VerificationException extends Error
	{
		private static final long serialVersionUID = 7484592083615005941L;

		public VerificationException() { this("EventData failed to be verified against EventInfo"); }

		public VerificationException(String message, Throwable cause) { super(message, cause); }
		public VerificationException(String message) { super(message); }
		public VerificationException(Throwable cause) { super(cause); }
	}
}
