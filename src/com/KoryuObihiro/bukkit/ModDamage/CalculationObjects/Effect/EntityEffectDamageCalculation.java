package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class EntityEffectDamageCalculation implements ModDamageCalculation 
{
	int value;
	protected boolean forAttacker;
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
}
