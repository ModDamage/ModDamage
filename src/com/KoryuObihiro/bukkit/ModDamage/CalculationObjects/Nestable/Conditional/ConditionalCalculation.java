package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.NestableCalculation;

public abstract class ConditionalCalculation extends NestableCalculation 
{
	final protected boolean inverted;
	public ConditionalCalculation(boolean inverted, List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.inverted = inverted;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{
		if(inverted?!condition(eventInfo):condition(eventInfo))
			doCalculations(eventInfo);
	}
	
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{
		if(inverted?!condition(eventInfo):condition(eventInfo))
			doCalculations(eventInfo);
	}
	
	protected abstract boolean condition(DamageEventInfo eventInfo);
	protected abstract boolean condition(SpawnEventInfo eventInfo);
}
