package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Set;

public class Binomial extends ConditionalCalculation
{
	private final Random random = new Random();
	private final int chance;
	public Binomial(int value)
	{ 
		chance = value;
		calculations = new ArrayList<ModDamageCalculation>();
		calculations.add(new Set(0));
	}
	public Binomial(int value, List<ModDamageCalculation> calculations)
	{ 
		if(value < 0 || value > 100) chance = 100;
		else chance = value;
		this.calculations = calculations;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo) 
	{
		return Math.abs(random.nextInt()%101) <= chance;
	}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo) 
	{
		return Math.abs(random.nextInt()%101) <= chance;
	}
}
