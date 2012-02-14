package com.ModDamage.Backend.Matching.DynamicIntegers;

import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routines;

public class DynamicRoutineInteger extends DynamicInteger
{
	private final Routines routines;
	final DataRef<Integer> defaultRef;
	
	public DynamicRoutineInteger(Routines routines, EventInfo info)
	{
		this.routines = routines;
		this.defaultRef = info.get(Integer.class, "-default");
	}
	
	@Override
	public int getValue(EventData data)
	{
		routines.run(data);
		
		return defaultRef.get(data);
	}
	
	@Override
	public String toString()
	{
		return "<routines>";//TODO Make this a bit better?
	}
	
}
