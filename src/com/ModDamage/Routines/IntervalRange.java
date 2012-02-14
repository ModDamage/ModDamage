package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class IntervalRange extends RandomRoutine 
{
	final protected DynamicInteger intervalValue, rangeValue;
	public IntervalRange(String configString, DataRef<Integer> defaultRef, ValueChangeType changeType, DynamicInteger base, DynamicInteger interval, DynamicInteger interval_range)
	{ 
		super(configString, defaultRef, changeType, base);
		intervalValue = interval;
		rangeValue = interval_range;
	}
	@Override
	public int getValue(EventData data)
	{
		return number.getValue(data) + (intervalValue.getValue(data) * (Math.abs(random.nextInt(rangeValue.getValue(data) + 1))));
	}
	
	public static void register()
	{
		ValueChangeRoutine.registerRoutine(Pattern.compile("range_int\\." + DynamicInteger.dynamicIntegerPart + "\\." + DynamicInteger.dynamicIntegerPart + "\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends ValueChangeRoutine.ValueBuilder
	{
		@Override
		public IntervalRange getNew(Matcher matcher, ValueChangeType changeType, EventInfo info)
		{ 
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1), info), 
						   match2 = DynamicInteger.getNew(matcher.group(2), info), 
						   match3 = DynamicInteger.getNew(matcher.group(3), info);
			DataRef<Integer> defaultRef = info.get(Integer.class, "-default");
			if(match1 != null && match2 != null && match3 != null && defaultRef != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Interval Range" + changeType.getStringAppend() + ": " + matcher.group(1) + ", " + matcher.group(2) + ", " + matcher.group(3));
				return new IntervalRange(matcher.group(), defaultRef, changeType, match1, match2, match3);
			}
			return null;
		}
	}
}