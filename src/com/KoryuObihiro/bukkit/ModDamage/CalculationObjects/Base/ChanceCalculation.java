package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;

import java.util.Random;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract class ChanceCalculation extends ModDamageCalculation
{
	int chance;
	final Random random = new Random();
}