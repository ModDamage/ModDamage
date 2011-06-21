package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityHealthLessThan extends EntityConditionalCalculation 
{
	int value;
	public EntityHealthLessThan(boolean forAttacker, int compareTo, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.value = compareTo;
		this.calculations = calculations;
	}
	@Override
	public int calculate(LivingEntity entity, int eventDamage) 
	{
		if(entity.getHealth() < value)
			return calculate(eventDamage);
		return eventDamage;
	}
}
