	package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class WorldCalculationRoutine extends CalculationRoutine<World>
{
	public WorldCalculationRoutine(String configString, List<Routine> routines)
	{
		super(configString, routines);
	}
	@Override
	protected World getAffectedObject(TargetEventInfo eventInfo){ return eventInfo.world;}
}
