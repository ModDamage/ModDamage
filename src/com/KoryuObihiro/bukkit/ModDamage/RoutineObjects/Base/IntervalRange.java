package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class IntervalRange extends RandomRoutine 
{
	protected DynamicInteger baseValue, intervalValue, rangeValue;
	public IntervalRange(String configString, DynamicInteger base, DynamicInteger interval, DynamicInteger interval_range)
	{ 
		super(configString);
		baseValue = base;
		intervalValue = interval;
		rangeValue = interval_range;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue += baseValue.getValue(eventInfo) + (intervalValue.getValue(eventInfo) * (Math.abs(random.nextInt()%(rangeValue.getValue(eventInfo) + 1))));}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("range_int\\." + DynamicInteger.dynamicIntegerPart + "\\." + DynamicInteger.dynamicIntegerPart + "\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public IntervalRange getNew(Matcher matcher)
		{ 
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1)), match2 = DynamicInteger.getNew(matcher.group(2)), match3 = DynamicInteger.getNew(matcher.group(3));
			if(match1 != null && match2 != null && match3 != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Interval range: " + matcher.group(1) + ", " + matcher.group(2) + ", " + matcher.group(3));
				return new IntervalRange(matcher.group(), match1, match2, match3);
			}
			return null;
		}
	}
}