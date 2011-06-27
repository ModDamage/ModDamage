package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class EntityEffectDamageCalculation extends DamageCalculation 
{
	protected boolean forAttacker;
	
	@Override
	public abstract void calculate(DamageEventInfo eventInfo);
}
