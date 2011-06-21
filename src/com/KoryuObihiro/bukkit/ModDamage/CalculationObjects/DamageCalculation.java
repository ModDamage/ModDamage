package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import org.bukkit.entity.LivingEntity;

public abstract class DamageCalculation
{
	public abstract int calculate(LivingEntity target, LivingEntity attacker, int eventDamage);
}