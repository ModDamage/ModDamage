package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Set;

public class Binomial extends ConditionalSpawnCalculation
{
	final Random random = new Random();
	private final int chance;
	public Binomial(int value)
	{ 
		chance = value;
		calculations = new ArrayList<SpawnCalculation>();
		calculations.add(new Set(0));
	}
	public Binomial(int value, List<SpawnCalculation> calculations)
	{ 
		if(value < 0 || value > 100) chance = 100;
		else chance = value;
		this.calculations = calculations;
	}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo) 
	{
		return Math.abs(random.nextInt()%101) <= chance;
	}
}