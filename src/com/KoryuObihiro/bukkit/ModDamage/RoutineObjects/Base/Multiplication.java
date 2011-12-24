package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

public class Multiplication extends ValueChangeRoutine 
{
	private int multiplicationValue;
	public Multiplication(String configString, ValueChangeType changeType, DynamicInteger value)
	{ 
		super(configString, changeType, value);
	}
	@Override
	public int getValue(TargetEventInfo eventInfo){ return eventInfo.eventValue * multiplicationValue;}
	
	public static void register()
	{
		ValueChangeRoutine.registerRoutine(Pattern.compile("(?:(?:mult(?:iply)?)\\.|\\*)(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends ValueChangeRoutine.ValueBuilder
	{
		@Override
		public Multiplication getNew(Matcher matcher, ValueChangeType changeType)
		{ 
			DynamicInteger match = DynamicInteger.getNew(matcher.group(1));
			if(match != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Multiply" + changeType.getStringAppend() + ": " + matcher.group(1));
				return new Multiplication(matcher.group(), changeType, match);
			}
			return null;
		}
	}
}
