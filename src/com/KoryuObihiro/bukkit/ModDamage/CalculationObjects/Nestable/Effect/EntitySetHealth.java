package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;


import java.util.List;

import org.bukkit.entity.Creature;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntitySetHealth extends EntityEffectDamageCalculation 
{
	public EntitySetHealth(boolean forAttacker, List<ModDamageCalculation> calculations)
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
			doCalculations(eventInfo);
			(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setHealth(value);
			eventInfo.eventDamage = value;
		}
		else (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setHealth(value);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			value = eventInfo.eventHealth;
			doCalculations(eventInfo);
			((Creature)eventInfo.entity).setHealth(eventInfo.eventHealth);
			eventInfo.eventHealth = value;
		}
		else ((Creature)eventInfo.entity).setHealth(value);
	}
}
