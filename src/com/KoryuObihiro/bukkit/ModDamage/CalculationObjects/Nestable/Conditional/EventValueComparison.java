package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;


import java.util.List;
import java.util.regex.Pattern;

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
		super(inverted, calculations);
		this.comparisonType = comparisonType;
		this.value = value;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return ComparisonType.compare(comparisonType, eventInfo.eventDamage, value);}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo){ return ComparisonType.compare(comparisonType, eventInfo.eventHealth, value);}
	
	public static void register()
	{
		CalculationUtility.register(EventValueComparison.class, Pattern.compile(CalculationUtility.ifPart + "value" + CalculationUtility.comparisonPart + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}

}
