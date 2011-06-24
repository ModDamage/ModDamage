package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class DamageGreaterThanEquals extends DamageConditionalCalculation
{
	final int value;
	public DamageGreaterThanEquals(int value, List<DamageCalculation> calculations)
	{ 
		this.value = value;
		this.calculations = calculations;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return eventInfo.eventDamage >= value;}
}
