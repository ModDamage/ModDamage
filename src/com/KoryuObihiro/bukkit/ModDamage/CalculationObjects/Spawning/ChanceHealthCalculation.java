package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning;

import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

abstract public class ChanceHealthCalculation extends SpawnCalculation
{
	final Random random = new Random();
	@Override
	public abstract void calculate(SpawnEventInfo eventInfo);
}
