package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class DamageGreaterThan extends ConditionalDamageCalculation
{
	final int value;
	public DamageGreaterThan(int value, List<DamageCalculation> calculations)
	{ 
		this.value = value;
		this.calculations = calculations;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return eventInfo.eventDamage > value;}
}
