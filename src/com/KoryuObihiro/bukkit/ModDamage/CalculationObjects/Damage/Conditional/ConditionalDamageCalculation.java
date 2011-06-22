package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class ConditionalDamageCalculation extends DamageCalculation 
{
	protected List<DamageCalculation> calculations;
	public int makeCalculations(EventInfo eventInfo, int eventDamage)
	{
		int result = eventDamage;
		for(DamageCalculation calculation : calculations)
			result = calculation.calculate(eventInfo, result);
		return result;
	}
}
