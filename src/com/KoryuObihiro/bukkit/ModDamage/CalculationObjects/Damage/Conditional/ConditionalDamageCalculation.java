package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


public abstract class ConditionalDamageCalculation
{
	public abstract int calculate(LivingEntity target, Entity attacker, int eventDamage);
}
