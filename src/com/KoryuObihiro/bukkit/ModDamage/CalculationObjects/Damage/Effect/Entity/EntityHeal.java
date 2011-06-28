package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityHeal extends EntityEffectDamageCalculation 
{
	int power;
	public EntityHeal(boolean forAttacker, List<DamageCalculation> calculations)
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
			power = eventInfo.eventDamage;
			for(DamageCalculation calculation : calculations)
				calculation.calculate(eventInfo);
			if(forAttacker) eventInfo.entity_attacker.setHealth(eventInfo.entity_attacker.getHealth() + eventInfo.eventDamage);
			else eventInfo.entity_target.setHealth(eventInfo.entity_target.getHealth() + eventInfo.eventDamage);
			eventInfo.eventDamage = power;
		}
		else eventInfo.entity_attacker.setHealth(eventInfo.entity_attacker.getHealth() + eventInfo.eventDamage);
	}
}
