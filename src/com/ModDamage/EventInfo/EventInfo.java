package com.ModDamage.EventInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.ModDamage;

import com.ModDamage.Parsing.EventDataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.misc.Multimap;

public abstract class EventInfo
{
	public abstract int getSize();
	
	
	protected abstract int myGetIndex(Class<?> cls, String name);
	public abstract Class<?> getClass(int index);

	public abstract Multimap<String, Class<?>> getAllNames();
	public abstract Set<String> getAllNames(Class<?> cls);
	public abstract Set<String> getAllNames(Class<?> cls, String name);

	protected abstract void verify(EventData data);
	
	public abstract int hashCode();
	public abstract boolean equals(Object other);
	

	public List<List<String>> getNamesLists()
	{
		List<List<String>> namesLists = new ArrayList<List<String>>(getSize());
		for (int i = 0; i < getSize(); i++)
			namesLists.add(new ArrayList<String>());
		
		fillNamesLists(namesLists, 0);
		return namesLists;
	}
	protected abstract void fillNamesLists(List<List<String>> namesLists, int offset);
	
	
	
	public <T> EventDataProvider<T> get(Class<T> cls, String name) { return get(cls, name, true); }
	@SuppressWarnings("unchecked")
	public <T> EventDataProvider<T> get(Class<T> cls, String name, boolean complain)
	{
		int index = getIndex(cls, name, complain);
		if (index == -1) return null;
		return new EventDataProvider<T>(cls, (Class<? extends T>) getClass(index), name, index);
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
	
	public abstract IDataProvider<Number> getLocal(String name);
	public abstract int getNumLocals();
	
	
	public EventData makeData(Object... objs) { return makeData(objs, false); }
	public EventData makeData(Object[] objs, boolean dummy) { return makeChainedData(null, objs); }

	public EventData makeChainedData(EventData parent, Object... objs) { return makeChainedData(parent, objs, false); }
	public EventData makeChainedData(EventData parent, Object[] objs, boolean dummy)
	{
		EventData data = new EventData(parent, getNumLocals(), objs);
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
