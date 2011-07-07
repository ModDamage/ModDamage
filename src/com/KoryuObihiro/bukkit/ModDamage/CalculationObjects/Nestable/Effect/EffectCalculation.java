package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.NestableCalculation;

abstract public class EffectCalculation<T> extends NestableCalculation 
{
	EffectCalculation(List<ModDamageCalculation> calculations)
	{
		super(calculations);
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
	abstract void applyEffect(T t);
}
