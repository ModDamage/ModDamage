package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class RoutineMatch extends IntegerMatch 
{
	protected final List<Routine> routines;
	
	protected RoutineMatch(List<Routine> routines)
	{
		super();
		this.routines = routines;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		int temp1 = eventInfo.eventValue, temp2 = 0;
		eventInfo.eventValue = 0;
		for(Routine routine : routines)
			routine.run(eventInfo);
		temp2 = eventInfo.eventValue;
		eventInfo.eventValue = temp1;
		return temp2;
	}
}
