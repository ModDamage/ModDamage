	package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class WorldCalculatedEffectRoutine extends CalculationRoutine<World>
{
	public WorldCalculatedEffectRoutine(List<Routine> routines)
	{
		super(routines);
	}
	@Override
	protected World getAffectedObject(TargetEventInfo eventInfo){ return eventInfo.world;}
}
