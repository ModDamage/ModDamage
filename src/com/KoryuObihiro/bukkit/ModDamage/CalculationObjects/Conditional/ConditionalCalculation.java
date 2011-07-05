package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public abstract class ConditionalCalculation implements ModDamageCalculation 
{
	protected boolean inverted;
	protected List<ModDamageCalculation> calculations;
	protected void makeCalculations(DamageEventInfo eventInfo)
	{
		for(ModDamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
	protected void makeCalculations(SpawnEventInfo eventInfo)
	{
		for(ModDamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{
		if((inverted?!condition(eventInfo):condition(eventInfo)))
			makeCalculations(eventInfo);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{
		if((inverted?!condition(eventInfo):condition(eventInfo)))
			makeCalculations(eventInfo);
	}
	protected abstract boolean condition(DamageEventInfo eventInfo);
	protected abstract boolean condition(SpawnEventInfo eventInfo);
}
