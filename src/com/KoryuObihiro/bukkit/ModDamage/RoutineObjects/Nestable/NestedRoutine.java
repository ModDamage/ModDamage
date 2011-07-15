package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class NestedRoutine extends Routine 
{
	final protected List<Routine> calculations;
	public NestedRoutine(List<Routine> calculations)
	{
		this.calculations = calculations;
	}
	protected void doCalculations(DamageEventInfo eventInfo)
	{
		for(Routine calculation : calculations)
			calculation.run(eventInfo);
	}
	protected void doCalculations(SpawnEventInfo eventInfo)
	{
		for(Routine calculation : calculations)
			calculation.run(eventInfo);
	}
	@Override
	public void run(DamageEventInfo eventInfo){ doCalculations(eventInfo);}
	@Override
	public void run(SpawnEventInfo eventInfo){ doCalculations(eventInfo);}
}
