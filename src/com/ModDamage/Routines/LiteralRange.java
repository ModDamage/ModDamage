package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class LiteralRange extends RandomRoutine 
{
	private final DynamicInteger upper;
	public LiteralRange(String configString, DataRef<Integer> defaultRef, ValueChangeType changeType, DynamicInteger lower, DynamicInteger upper)
	{ 
		super(configString, defaultRef, changeType, lower);
		this.upper = upper;
	}
	
	@Override
	public int getValue(EventData data)
	{
		int lower = number.getValue(data);
		return lower + Math.abs(random.nextInt(upper.getValue(data) - lower + 1));
	}
	
	public static void register()
	{
		ValueChangeRoutine.registerRoutine(Pattern.compile("range\\." + DynamicInteger.dynamicIntegerPart + "\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends ValueChangeRoutine.ValueBuilder
	{	
		@Override
		public LiteralRange getNew(Matcher matcher, ValueChangeType changeType, EventInfo info)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1), info), 
					       match2 = DynamicInteger.getNew(matcher.group(2), info);
			DataRef<Integer> defaultRef = info.get(Integer.class, "-default");
			if(match1 != null && match2 != null && defaultRef != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Literal Range" + changeType.getStringAppend() + ": (" + matcher.group(1) + ", " + matcher.group(2) + ")");
				return new LiteralRange(matcher.group(), defaultRef, changeType, match1, match2);
			}
			return null;
		}
	}
}
