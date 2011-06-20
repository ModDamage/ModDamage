package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import java.util.Random;

abstract class ChanceCalculation extends DamageCalculation
{
	int chance;
	final Random random = new Random();
	public abstract int calculate(int eventDamage);
}