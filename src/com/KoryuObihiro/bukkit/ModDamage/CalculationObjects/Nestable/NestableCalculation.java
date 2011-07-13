package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class NestableCalculation extends ModDamageCalculation 
{
	final protected List<ModDamageCalculation> calculations;
	public NestableCalculation(List<ModDamageCalculation> calculations)
	{
		this.calculations = calculations;
	}
	protected void doCalculations(DamageEventInfo eventInfo)
	{
		for(ModDamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
	protected void doCalculations(SpawnEventInfo eventInfo)
	{
		for(ModDamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
	@Override
	public void calculate(DamageEventInfo eventInfo){ doCalculations(eventInfo);}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ doCalculations(eventInfo);}
}
