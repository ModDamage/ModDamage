package com.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class DiceRoll extends RandomRoutine 
{
	protected DiceRoll(String configString, ValueChangeType changeType)
	{
		super(configString, changeType, DynamicInteger.getNew("event_value"));
	}
	protected DiceRoll(String configString, ValueChangeType changeType, DynamicInteger rollValue) 
	{
		super(configString, changeType, rollValue);
	}

	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		return Math.abs(random.nextInt()%(number.getValue(eventInfo) + 1));
	}
	
	public static void register()
	{
		ValueChangeRoutine.registerRoutine(Pattern.compile("roll(?:\\.(" + DynamicInteger.dynamicIntegerPart + "))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends ValueChangeRoutine.ValueBuilder
	{
		@Override
		public DiceRoll getNew(Matcher matcher, ValueChangeType changeType)
		{ 
			if(!matcher.group(1).equalsIgnoreCase(""))
			{
				DynamicInteger match = DynamicInteger.getNew(matcher.group(2));
				if(match != null)
				{
					ModDamage.addToLogRecord(OutputPreset.INFO, "Dice Roll" + changeType.getStringAppend() + ": " + matcher.group(1));
					return new DiceRoll(matcher.group(), changeType, match);
				}
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Dice Roll: roll existing");
				return new DiceRoll(matcher.group(), changeType);
			}
			return null;
		}
	}
}