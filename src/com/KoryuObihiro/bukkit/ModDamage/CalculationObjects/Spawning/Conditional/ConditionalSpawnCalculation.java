package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public abstract class ConditionalSpawnCalculation extends SpawnCalculation
{
	protected boolean inverted;
	protected List<SpawnCalculation> calculations;
	public void makeCalculations(SpawnEventInfo eventInfo)
	{
		for(SpawnCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
	public void calculate(SpawnEventInfo eventInfo)
	{
		if((inverted?!condition(eventInfo):condition(eventInfo)))
			makeCalculations(eventInfo);
	}
	protected abstract boolean condition(SpawnEventInfo eventInfo);
}
