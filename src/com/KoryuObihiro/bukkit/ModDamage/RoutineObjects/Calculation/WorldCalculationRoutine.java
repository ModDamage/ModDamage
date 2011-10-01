package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

abstract public class WorldCalculationRoutine extends CalculationRoutine<World>
{
	public WorldCalculationRoutine(String configString, IntegerMatch match)
	{
		super(configString, match);
	}
	@Override
	protected World getAffectedObject(TargetEventInfo eventInfo){ return eventInfo.world;}
}
