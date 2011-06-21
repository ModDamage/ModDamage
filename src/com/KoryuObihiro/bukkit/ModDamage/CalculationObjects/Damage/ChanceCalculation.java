package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import java.util.Random;

abstract class ChanceCalculation extends MathDamageCalculation
{
	int chance;
	final Random random = new Random();
}