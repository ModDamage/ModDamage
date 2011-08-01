package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

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
	public boolean condition(TargetEventInfo eventInfo){ return comparisonType.compare(eventInfo.eventValue, value);}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EventValueComparison.class, Pattern.compile("(!)?eventvalue" + ModDamage.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EventValueComparison getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EventValueComparison(matcher.group(1) != null, ComparisonType.matchType(matcher.group(3)), Integer.parseInt(matcher.group(4)));
		return null;
	}
}
