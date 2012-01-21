package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.Collection;

import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Routine;

public class DynamicRoutineInteger extends DynamicInteger
{
	private final Collection<Routine> routines;
	
	public DynamicRoutineInteger(Collection<Routine> routines)
	{
		this.routines = routines;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		int oldvalue = eventInfo.eventValue;
		eventInfo.eventValue = 0;
		
		for(Routine routine : routines)
			routine.run(eventInfo);
		
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
