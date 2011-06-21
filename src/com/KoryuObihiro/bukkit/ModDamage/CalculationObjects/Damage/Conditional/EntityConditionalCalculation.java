package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import org.bukkit.entity.LivingEntity;

public abstract class EntityConditionalCalculation extends ConditionalDamageCalculation
{
	protected boolean isEntityConditional = true;
	protected boolean forAttacker;
	public boolean evaluateAttacker(){ return forAttacker;}
	public abstract int calculate(LivingEntity entity, int eventDamage);
}
