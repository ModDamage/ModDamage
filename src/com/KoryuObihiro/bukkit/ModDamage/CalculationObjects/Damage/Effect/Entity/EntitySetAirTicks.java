package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import org.bukkit.entity.Creature;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntitySetAirTicks extends EntityEffectDamageCalculation 
{
	int ticks;
	public EntitySetAirTicks(boolean forAttacker, List<DamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
		this.ticks = 0;
	}
	public EntitySetAirTicks(boolean forAttacker, int ticks)
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
			for(DamageCalculation calculation : calculations)
				calculation.calculate(eventInfo);
			((Creature)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)).setRemainingAir(eventInfo.eventDamage);
			eventInfo.eventDamage = ticks;
		}
		else ((Creature)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)).setRemainingAir(ticks);
	}
}
