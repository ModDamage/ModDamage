package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityExplode extends EntityEffectDamageCalculation 
{
	public EntityExplode(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		this.forAttacker = forAttacker;
		this.calculations = calculations;
		this.value = 0;
	}
	public EntityExplode(boolean forAttacker, int power)
	{
		this.forAttacker = forAttacker;
		this.value = power;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{
		if(calculations != null)
		{
			value = eventInfo.eventDamage;
			doCalculations(eventInfo);
			eventInfo.world.createExplosion((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getEyeLocation(), eventInfo.eventDamage);
			eventInfo.eventDamage = value;
			value = 0;
		}
		else eventInfo.world.createExplosion((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getEyeLocation(), value);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{
		if(calculations != null)
		{
			value = eventInfo.eventHealth;
			doCalculations(eventInfo);
			eventInfo.world.createExplosion(eventInfo.entity.getEyeLocation(), eventInfo.eventHealth);
			eventInfo.eventHealth = value;
			value = 0;
		}
		else eventInfo.world.createExplosion(eventInfo.entity.getEyeLocation(), value);
	}
}
