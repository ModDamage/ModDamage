package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntitySetHealth extends EntityEffectDamageCalculation 
{
	int value;
	public EntitySetHealth(boolean forAttacker, List<DamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	public EntitySetHealth(boolean forAttacker, int value)
	{
		this.forAttacker = forAttacker;
		this.value = value;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			value = eventInfo.eventDamage;
			for(DamageCalculation calculation : calculations)
				calculation.calculate(eventInfo);
			(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setHealth(value);
			eventInfo.eventDamage = value;
		}
		else (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setHealth(value);
	}
}
