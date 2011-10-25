package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DynamicRoutineInteger extends DynamicInteger
{
	private final List<Routine> routines;
	
	public DynamicRoutineInteger(List<Routine> routines, boolean isNegative)
	{
		super(isNegative, false);
		this.routines = routines;
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo)
	{
		for(Routine routine : routines)
			routine.run(eventInfo);
		return (isNegative?-1:1) * eventInfo.eventValue;
	}
	
	@Override
	public String toString()
	{
		return isNegative?"-":"" + "<some-routines>";//TODO Make this a bit better?
	}
	
}
