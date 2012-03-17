package com.ModDamage.Routines;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class DiceRoll extends ValueChange 
{
	protected final Random random = new Random();
	
	protected DiceRoll(String configString, DataRef<IntRef> defaultRef, ValueChangeType changeType, IntegerExp rollValue) 
	{
		super(configString, defaultRef, changeType, rollValue);
	}

	@Override
	public int getValue(EventData data) throws BailException
	{
		return Math.abs(random.nextInt()%(number.getValue(data) + 1));
	}
	
	public static void register()
	{
		ValueChange.registerRoutine(Pattern.compile("roll\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends ValueChange.ValueBuilder
	{
		@Override
		public DiceRoll getNew(Matcher matcher, ValueChangeType changeType, EventInfo info)
		{
			IntegerExp match = IntegerExp.getNew(matcher.group(1), info);
			DataRef<IntRef> defaultRef = info.get(IntRef.class, "-default");
			if(match != null && defaultRef != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Dice Roll" + changeType.getStringAppend() + ": " + matcher.group(1));
				ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "This form is deprecated. Please use 'roll(" + matcher.group(1) + ") instead");
				
				return new DiceRoll(matcher.group(), defaultRef, changeType, match);
			}
			return null;
		}
	}
}