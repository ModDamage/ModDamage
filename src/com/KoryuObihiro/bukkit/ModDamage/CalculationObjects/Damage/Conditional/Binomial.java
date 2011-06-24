package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Set;

public class Binomial extends DamageConditionalCalculation
{
	private final Random random = new Random();
	private final int chance;
	public Binomial(int value)
	{ 
		chance = value;
		calculations = new ArrayList<DamageCalculation>();
		calculations.add(new Set(0));
	}
	public Binomial(int value, List<DamageCalculation> calculations)
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
}
