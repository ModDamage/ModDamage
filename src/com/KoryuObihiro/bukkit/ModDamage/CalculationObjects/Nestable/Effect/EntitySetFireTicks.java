package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;


import java.util.List;

import org.bukkit.entity.Creature;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntitySetFireTicks extends EntityEffectDamageCalculation 
{
	int ticks;
	public EntitySetFireTicks(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
		this.ticks = 0;
	}
	public EntitySetFireTicks(boolean forAttacker, int ticks)
	{
		this.forAttacker = forAttacker;
		this.ticks = ticks;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			ticks = eventInfo.eventDamage;
			doCalculations(eventInfo);
			(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setFireTicks(eventInfo.eventDamage);
			eventInfo.eventDamage = ticks;
		}
		else (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).setFireTicks(ticks);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			ticks = eventInfo.eventHealth;
			doCalculations(eventInfo);
			((Creature)eventInfo.entity).setFireTicks(eventInfo.eventHealth);
			eventInfo.eventHealth = ticks;
		}
		else ((Creature)eventInfo.entity).setFireTicks(ticks);
	}
}
