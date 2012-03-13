package com.ModDamage.EventInfo;

import java.util.Set;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;

public abstract class EventInfo
{
	public abstract int getSize();
	
	
	protected abstract int myGetIndex(Class<?> cls, String name);
	protected abstract Class<?> getClass(int index);
	
	public abstract Set<String> getAllNames(Class<?> cls);
	public abstract Set<String> getAllNames(Class<?> cls, String name);

	protected abstract void verify(EventData data);
	
	public abstract int hashCode();
	public abstract boolean equals(Object other);
	
	
	
	public <T> DataRef<T> get(Class<T> cls, String name) { return get(cls, name, true); }
	public <T> DataRef<T> get(Class<T> cls, String name, boolean complain)
	{
		int index = getIndex(cls, name, complain);
		if (index == -1) return null;
		return new DataRef<T>(cls, getClass(index), name, index);
	}
	
	public int getIndex(Class<?> cls, String name, boolean complain) {
		int index = myGetIndex(cls, name);
		if (complain && index == -1)
		{
			StringBuilder names = new StringBuilder();
			Set<String> allNames = getAllNames(cls);
			if (allNames != null)
				for (String n : allNames)
				{
					if (!n.startsWith("-"))
					{
						if (names.length() > 0)
							names.append(" or ");
						names.append(n);
					}
				}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown "+cls.getSimpleName()+" named '"+ name +"'" + (names.length() > 0? ", did you mean " + names + "?" : ""));
		}
		return index;
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
