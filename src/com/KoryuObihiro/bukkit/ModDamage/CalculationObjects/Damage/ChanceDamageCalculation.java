package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import java.util.Random;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

abstract class ChanceDamageCalculation extends DamageCalculation
{
	int chance;
	final Random random = new Random();
}