package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class EntityEffectCalculation extends EffectCalculation<Entity>
{
	final int value;
	final protected boolean forAttacker;
	
	public EntityEffectCalculation(boolean forAttacker, int value, List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.forAttacker = forAttacker;
		this.value = value;
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
}
