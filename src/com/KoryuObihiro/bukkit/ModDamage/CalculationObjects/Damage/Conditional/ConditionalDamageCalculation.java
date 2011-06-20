package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.DamageCalculation;

public abstract class ConditionalDamageCalculation
{
	protected List<DamageCalculation> calculations;
	protected boolean forAttacker;
	public abstract int calculate(LivingEntity target, LivingEntity attacker, int eventDamage);
	protected int makeCalculations(List<DamageCalculation> calculations, int eventDamage)
	{
		int result = eventDamage;
		for(DamageCalculation damageCalculation : calculations)
			result = damageCalculation.calculate(result);
		return result;
	}
}
