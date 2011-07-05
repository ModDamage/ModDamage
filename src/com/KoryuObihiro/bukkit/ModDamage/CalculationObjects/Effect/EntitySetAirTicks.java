package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Effect;


import java.util.List;

import org.bukkit.entity.Creature;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntitySetAirTicks extends EntityEffectDamageCalculation 
{
	public EntitySetAirTicks(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
		this.value = 0;
	}
	public EntitySetAirTicks(boolean forAttacker, int ticks)
	{
		this.forAttacker = forAttacker;
		this.value = ticks;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			value = eventInfo.eventDamage;
			makeCalculations(eventInfo);
			((Creature)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)).setRemainingAir(eventInfo.eventDamage);
			eventInfo.eventDamage = value;
		}
		else ((Creature)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)).setRemainingAir(value);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			value = eventInfo.eventHealth;
			makeCalculations(eventInfo);
			((Creature)eventInfo.entity).setRemainingAir(eventInfo.eventHealth);
			eventInfo.eventHealth = value;
		}
		else ((Creature)eventInfo.entity).setRemainingAir(value);
	}
}
