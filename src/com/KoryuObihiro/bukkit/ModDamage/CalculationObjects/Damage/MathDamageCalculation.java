package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class MathDamageCalculation extends DamageCalculation 
{
	public abstract int calculate(int eventDamage);
}
