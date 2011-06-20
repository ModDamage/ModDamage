package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health.Conditional;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


public abstract class ConditionalHealthCalculation
{
	public abstract int calculate(LivingEntity target, Entity attacker, int eventDamage);
}
