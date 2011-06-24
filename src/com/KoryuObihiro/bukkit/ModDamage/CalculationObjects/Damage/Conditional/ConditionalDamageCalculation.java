package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class ConditionalDamageCalculation extends DamageCalculation 
{
	protected List<DamageCalculation> calculations;
	public void makeCalculations(EventInfo eventInfo)
	{
		for(DamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
}
