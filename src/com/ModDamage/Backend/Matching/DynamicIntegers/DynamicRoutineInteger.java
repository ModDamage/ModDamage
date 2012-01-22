package com.ModDamage.Backend.Matching.DynamicIntegers;

import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Routines;

public class DynamicRoutineInteger extends DynamicInteger
{
	private final Routines routines;
	
	public DynamicRoutineInteger(Routines routines)
	{
		this.routines = routines;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		int oldvalue = eventInfo.eventValue;
		eventInfo.eventValue = 0;
		
		routines.run(eventInfo);
		
		int value = eventInfo.eventValue;
		eventInfo.eventValue = oldvalue;
		return value;
	}
	
	@Override
	public String toString()
	{
		return "<routines>";//TODO Make this a bit better?
	}
	
}
