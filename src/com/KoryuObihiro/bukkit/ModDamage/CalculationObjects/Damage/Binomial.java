package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class Binomial extends ChanceCalculation
{
	private List<DamageCalculation> calculations;
	public Binomial(int value)
	{ 
		chance = value;
		if(chance < 0 || chance > 100) chance = 100;
		calculations.add(new Set(0));
	}
	public Binomial(int value, List<DamageCalculation> list)
	{ 
		chance = value;
		if(chance < 0 || chance > 100) chance = 100;
		calculations.addAll(list);
	}
	public void makeCalculations(EventInfo eventInfo)
	{
		for(DamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
	@Override
	public void calculate(EventInfo eventInfo){ if(Math.abs(random.nextInt()%101) <= chance) makeCalculations(eventInfo);}
}
