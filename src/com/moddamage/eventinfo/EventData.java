package com.ModDamage.EventInfo;

import com.ModDamage.LogUtil;

public class EventData implements Cloneable
{
	public final EventData parent;
	public final int start;
	public final Object[] objects;
	public final Number[] locals;
	
//	EventData(int numLocals, Object... objects) {
//		this.parent = null;
//		this.start = 0;
//		this.objects = objects;
//		this.locals = new int[numLocals];
//	}
	EventData(EventData parent, int numLocals, Object... objects)
	{
		this.parent = parent;
		this.start = parent == null? 0 : parent.start + parent.objects.length;
		this.objects = objects;
		if (parent != null) locals = null;
		else locals = new Number[numLocals];
	}
	
	EventData(EventData other) // copy constructor
	{
		parent = (other.parent != null)? other.parent.clone() : null;
		start = other.start;
		objects = other.objects.clone();
		locals = (other.locals != null)? other.locals.clone() : null;
	}
	
//	private EventData(EventData parent, int start, Object[] objects)
//	{
//		this.parent = parent;
//		this.start = start;
//		this.objects = objects;
//		this.locals = null;
//	}
	
	public Object get(int i)
	{
		if (i - start >= objects.length) {
			LogUtil.error("Bad index of "+i+" / "+start+"+"+objects.length);
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
		LogUtil.error("Bad cast of "+o+" to "+cls.getSimpleName());
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
			LogUtil.error("Setting bad index "+i+" to "+obj+" type "+cls.getSimpleName());
	}
	
	public Number getLocal(int localIndex) {
		if (locals == null) return parent.getLocal(localIndex);
		Number num = locals[localIndex];
		if (num == null)
			num = 0;
		return num;
	}
	
	public void setLocal(int localIndex, Number value) {
		if (locals == null) parent.setLocal(localIndex, value);
		else if (value == null) locals[localIndex] = 0;
		else locals[localIndex] = value;
	}
	
	
	@Override public EventData clone()
	{
		return new EventData(this);
	}
}
