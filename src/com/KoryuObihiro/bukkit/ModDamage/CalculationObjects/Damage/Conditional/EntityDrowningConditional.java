package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityDrowningConditional extends EntityConditionalCalculation 
{
	public EntityDrowningConditional(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public int calculate(LivingEntity entity, int eventDamage) 
	{
		if(entity.getRemainingAir() == 0)
			return calculate(eventDamage);
		return eventDamage;
	}
}
