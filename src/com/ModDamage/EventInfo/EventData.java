package com.ModDamage.EventInfo;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class EventData implements Cloneable
{
	public final EventData parent;
	public final int start;
	public final Object[] objects;
	
	EventData(Object... objects) {
		this(null, objects);
	}
	EventData(EventData parent, Object... objects)
	{
		this.parent = parent;
		this.start = parent == null? 0 : parent.start + parent.objects.length;
		this.objects = objects;
	}
	
	private EventData(EventData parent, int start, Object[] objects)
	{
		this.parent = parent;
		this.start = start;
		this.objects = objects;
	}
	
	public Object get(int i)
	{
		if (i - start >= objects.length) {
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad index of "+i+" / "+start+"+"+objects.length);
			return null;
		}
		if (i >= start) return objects[i - start];
		return parent.get(i);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> cls, int i)
	{
		Object o = get(i);
		if (o == null) return null;
		if (cls.isInstance(o)) return (T) o;
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad cast of "+o+" to "+cls.getSimpleName());
		return null;
	}
	
	/**
	 * Make sure obj is the right type!
	 */
	public void set(int i, Object obj)
	{
		if (i >= start)
			objects[i - start] = obj;
		else 
			parent.set(i, obj);
	}
	
	/**
	 * Make sure obj is the right type!
	 */
	public <T> void set(Class<T> cls, int i, T obj)
	{
		if (i >= start)
			objects[i - start] = obj;
		else if (parent != null)
			parent.set(i, obj);
		else
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Setting bad index "+i+" to "+obj+" type "+cls.getSimpleName());
	}
	
	
	@Override public EventData clone()
	{
		return new EventData(parent, start, objects.clone());
	}
}
