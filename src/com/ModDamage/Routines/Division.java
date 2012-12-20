package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class Division extends ValueChange 
{
	public Division(String configString, ISettableDataProvider<Integer> defaultDP, ValueChangeType changeType, IDataProvider<Integer> value)
	{ 
		super(configString, defaultDP, changeType, value);
	}
	@Override
	public int getValue(Integer def, EventData data) throws BailException
	{
		return def / number.get(data);
	}
	
	public static void register()
	{
		ValueChange.registerRoutine(Pattern.compile("(?:div(?:ide)?\\.|\\\\|/)(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends ValueChange.ValueBuilder
	{
		@Override
		public Division getNew(Matcher matcher, ValueChangeType changeType, EventInfo info)
		{ 
			IDataProvider<Integer> match = DataProvider.parse(info, Integer.class, matcher.group(1));
			ISettableDataProvider<Integer> defaultDP = info.get(Integer.class, "-default");
			if(match != null && defaultDP != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Divide" + changeType.getStringAppend() + ": " + matcher.group(1));
				return new Division(matcher.group(), defaultDP, changeType, match);
			}
			return null;
		}
	}
}
