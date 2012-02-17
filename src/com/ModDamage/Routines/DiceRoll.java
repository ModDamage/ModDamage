package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DiceRoll extends RandomRoutine 
{
	protected DiceRoll(String configString, DataRef<IntRef> defaultRef, ValueChangeType changeType, DynamicInteger rollValue) 
	{
		super(configString, defaultRef, changeType, rollValue);
	}

	@Override
	public int getValue(EventData data)
	{
		return Math.abs(random.nextInt()%(number.getValue(data) + 1));
	}
	
	public static void register()
	{
		ValueChangeRoutine.registerRoutine(Pattern.compile("roll(?:\\.(" + DynamicInteger.dynamicIntegerPart + "))", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends ValueChangeRoutine.ValueBuilder
	{
		@Override
		public DiceRoll getNew(Matcher matcher, ValueChangeType changeType, EventInfo info)
		{
			DynamicInteger match = DynamicInteger.getNew(matcher.group(1), info);
			DataRef<IntRef> defaultRef = info.get(IntRef.class, "-default");
			if(match != null && defaultRef != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Dice Roll" + changeType.getStringAppend() + ": " + matcher.group(1));
				return new DiceRoll(matcher.group(), defaultRef, changeType, match);
			}
			return null;
		}
	}
}