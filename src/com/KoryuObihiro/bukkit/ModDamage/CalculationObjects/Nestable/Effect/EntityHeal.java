package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityHeal extends EntityEffectCalculation 
{
	public EntityHeal(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	public EntityHeal(boolean forAttacker, int power)
	{
		this.forAttacker = forAttacker;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			value = eventInfo.eventDamage;
			doCalculations(eventInfo);
			if(forAttacker) eventInfo.entity_attacker.setHealth(eventInfo.entity_attacker.getHealth() + eventInfo.eventDamage);
			else eventInfo.entity_target.setHealth(eventInfo.entity_target.getHealth() + eventInfo.eventDamage);
			eventInfo.eventDamage = value;
		}
		else eventInfo.entity_attacker.setHealth(eventInfo.entity_attacker.getHealth() + eventInfo.eventDamage);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			value = eventInfo.eventHealth;
			doCalculations(eventInfo);
			eventInfo.entity.setHealth(eventInfo.entity.getHealth() + eventInfo.eventHealth);
			eventInfo.eventHealth = value;
		}
		else eventInfo.entity.setHealth(eventInfo.entity.getHealth() + eventInfo.eventHealth);
	}
}
