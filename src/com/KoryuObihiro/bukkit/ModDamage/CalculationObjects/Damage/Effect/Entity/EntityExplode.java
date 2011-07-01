package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityExplode extends EntityEffectDamageCalculation 
{
	int power;
	public EntityExplode(boolean forAttacker, List<DamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
		this.power = 0;
	}
	public EntityExplode(boolean forAttacker, int power)
	{
		this.forAttacker = forAttacker;
		this.power = power;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{
		if(calculations != null)
		{
			power = eventInfo.eventDamage;
			for(DamageCalculation calculation : calculations)
				calculation.calculate(eventInfo);
			eventInfo.world.createExplosion((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getEyeLocation(), eventInfo.eventDamage);
			eventInfo.eventDamage = power;
			power = 0;
		}
		else eventInfo.world.createExplosion((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getEyeLocation(), power);
	}
}
