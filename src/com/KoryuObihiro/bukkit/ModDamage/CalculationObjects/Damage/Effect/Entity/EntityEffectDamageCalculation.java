package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class EntityEffectDamageCalculation extends DamageCalculation 
{
	protected boolean forAttacker;
	protected List<DamageCalculation> calculations;
}
