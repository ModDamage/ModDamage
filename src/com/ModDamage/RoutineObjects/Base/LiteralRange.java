package com.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class LiteralRange extends RandomRoutine 
{
	final protected DynamicInteger upper;
	public LiteralRange(String configString, ValueChangeType changeType, DynamicInteger lower, DynamicInteger upper)
	{ 
		super(configString, changeType, lower);
		this.upper = upper;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		int lower = number.getValue(eventInfo);
		return lower + Math.abs(random.nextInt(upper.getValue(eventInfo) - lower + 1));
	}
	
	public static void register()
	{
		ValueChangeRoutine.registerRoutine(Pattern.compile("range\\." + DynamicInteger.dynamicIntegerPart + "\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends ValueChangeRoutine.ValueBuilder
	{	
		@Override
		public LiteralRange getNew(Matcher matcher, ValueChangeType changeType)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1)), match2 = DynamicInteger.getNew(matcher.group(2));
			if(match1 != null && match2 != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Literal Range" + changeType.getStringAppend() + ": (" + matcher.group(1) + ", " + matcher.group(2) + ")");
				return new LiteralRange(matcher.group(), changeType, match1, match2);
			}
			return null;
		}
	}
}
