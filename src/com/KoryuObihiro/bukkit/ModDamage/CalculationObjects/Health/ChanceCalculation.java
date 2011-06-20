package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health;

import java.util.Random;

abstract public class ChanceCalculation extends HealthCalculation
{
	Random random = new Random();
	@Override
	public abstract int calculate();
}
