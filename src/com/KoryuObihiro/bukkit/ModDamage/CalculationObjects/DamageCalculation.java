package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import org.bukkit.entity.LivingEntity;

public abstract class DamageCalculation
{
	protected boolean forAttacker;
	public abstract int calculate(LivingEntity entity, int eventDamage);
}