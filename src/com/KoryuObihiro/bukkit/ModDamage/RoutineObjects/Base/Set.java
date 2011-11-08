package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Set extends CalculationRoutine
{
	public Set(String configString, DynamicInteger value)
	{ 
		super(configString, value);
	}
	
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = value.getValue(eventInfo);}
	
	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input){}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("set\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new BaseRoutineBuilder());
		CalculationRoutine.registerRoutine(Pattern.compile("set", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}

	
	protected static class BaseRoutineBuilder extends Routine.RoutineBuilder
	{	
		@Override
		public Set getNew(Matcher matcher)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1));
			if(match1 != null)
				return new Set(matcher.group(), match1);
			return null;
		}
	}
	protected static class NestedRoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public Set getNew(Matcher matcher, DynamicInteger routines)
		{ 
			return new Set(matcher.group(), routines);
		}
	}
}