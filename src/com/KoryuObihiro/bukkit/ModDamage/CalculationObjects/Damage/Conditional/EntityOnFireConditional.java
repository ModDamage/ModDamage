package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityOnFireConditional extends EntityConditionalCalculation 
{
	public EntityOnFireConditional(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage) 
	{
		if((forAttacker?attacker:target).getFireTicks() > 0)
			return makeCalculations(target, attacker, eventDamage);
		return 0;
	}
}
