package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EventValueComparison extends ConditionalCalculation
{
	protected final int value;
	protected final ComparisonType comparisonType;
	public EventValueComparison(boolean inverted, ComparisonType comparisonType, int value, List<ModDamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.comparisonType = comparisonType;
		this.value = value;
		this.calculations = calculations;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return CalculationUtility.compare(comparisonType, eventInfo.eventDamage, value);}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo){ return CalculationUtility.compare(comparisonType, eventInfo.eventHealth, value);}

}
