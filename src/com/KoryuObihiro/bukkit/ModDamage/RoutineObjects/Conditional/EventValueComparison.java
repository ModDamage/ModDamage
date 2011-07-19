package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

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
	
	public static void register(RoutineUtility routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EventValueComparison.class, Pattern.compile("value" + RoutineUtility.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}

}
