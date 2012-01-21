package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class Division extends ValueChangeRoutine 
{
	public Division(String configString, ValueChangeType changeType, DynamicInteger value)
	{
		super(configString, changeType, value);
	}
	@Override
	public int getValue(TargetEventInfo eventInfo){ return eventInfo.eventValue/number.getValue(eventInfo);}
	
	public static void register()
	{
		ValueChangeRoutine.registerRoutine(Pattern.compile("(?:div(?:ide)?\\.|\\\\|/)(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends ValueChangeRoutine.ValueBuilder
	{
		@Override
		public Division getNew(Matcher matcher, ValueChangeType changeType)
		{ 
			DynamicInteger match = DynamicInteger.getNew(matcher.group(1));
			if(match != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Division" + changeType.getStringAppend() + ": " + matcher.group(1));
				return new Division(matcher.group(), changeType, match);
			}
			return null;
		}
	}
}
