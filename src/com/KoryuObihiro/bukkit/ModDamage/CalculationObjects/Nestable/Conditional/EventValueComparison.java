package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;

public class EventValueComparison extends ConditionalStatement
{
	protected final int value;
	protected final ComparisonType comparisonType;
	public EventValueComparison(boolean inverted, ComparisonType comparisonType, int value)
	{ 
		super(inverted);
		this.comparisonType = comparisonType;
		this.value = value;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return comparisonType.compare(eventInfo.eventDamage, value);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return comparisonType.compare(eventInfo.eventHealth, value);}
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(EventValueComparison.class, Pattern.compile("value" + CalculationUtility.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}

}
