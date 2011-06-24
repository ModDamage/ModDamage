package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class DamageConditionalCalculation extends DamageCalculation 
{
	protected boolean inverted;
	protected List<DamageCalculation> calculations;
	public void makeCalculations(DamageEventInfo eventInfo)
	{
		for(DamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
	}
	public void calculate(DamageEventInfo eventInfo)
	{
		if((inverted?!condition(eventInfo):condition(eventInfo)))
			makeCalculations(eventInfo);
	}
	protected abstract boolean condition(DamageEventInfo eventInfo);
}
