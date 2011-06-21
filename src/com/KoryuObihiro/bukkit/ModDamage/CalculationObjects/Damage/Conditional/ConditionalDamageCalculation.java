package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class ConditionalDamageCalculation extends DamageCalculation 
{
	protected List<DamageCalculation> calculations;
	public int makeCalculations(LivingEntity target, LivingEntity attacker, int eventDamage)
	{
		int result = eventDamage;
		if(condition())
			for(DamageCalculation calculation : calculations)
				result = calculation.calculate(target, attacker, result);
		return result;
	}
	protected abstract boolean condition();
}
