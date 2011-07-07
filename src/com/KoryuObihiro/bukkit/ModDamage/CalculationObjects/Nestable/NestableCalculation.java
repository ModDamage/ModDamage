package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class NestableCalculation extends ModDamageCalculation 
{
	final protected List<ModDamageCalculation> calculations;
	protected NestableCalculation(List<ModDamageCalculation> calculations)
	{
		this.calculations = calculations;
	}
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
	public void calculate(DamageEventInfo eventInfo){ makeCalculations(eventInfo);}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ makeCalculations(eventInfo);}
}
