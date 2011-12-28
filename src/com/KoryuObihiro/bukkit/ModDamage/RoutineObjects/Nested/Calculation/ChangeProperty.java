package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public final class ChangeProperty extends CalculationRoutine
{	
	protected final DynamicInteger targetPropertyMatch;
	protected final boolean additive;
	public ChangeProperty(String configString, DynamicInteger value, DynamicInteger targetPropertyMatch, boolean additive)
	{
		super(configString, value);
		this.targetPropertyMatch = targetPropertyMatch;
		this.additive = additive;
	}

	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input)
	{
		targetPropertyMatch.setValue(eventInfo, value.getValue(eventInfo) + (additive?targetPropertyMatch.getValue(eventInfo):0));
	}
	
	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(.*)effect\\.(set|add)(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public ChangeProperty getNew(Matcher matcher, DynamicInteger routines)
		{
			DynamicInteger targetPropertyMatch = DynamicInteger.getNew(matcher.group(1) + "_" + matcher.group(3));
			if(targetPropertyMatch != null)
			{
				if(targetPropertyMatch.isSettable())
					return new ChangeProperty(matcher.group(), routines, targetPropertyMatch, matcher.group(2).equalsIgnoreCase("add"));
				else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Property \"" + matcher.group(3) + "\" of \"" + matcher.group(1) + "\" is not modifiable.");
			}
			return null;
		}
	}
}
