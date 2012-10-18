package com.ModDamage.Routines;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.ISettableDataProvider;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Nested.NestedRoutine;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClearList extends NestedRoutine
{
	private final ISettableDataProvider<List> listDP;

	protected ClearList(String configString, ISettableDataProvider<List> listDP)
	{
		super(configString);
		this.listDP = listDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		listDP.get(data).clear();
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("clear.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ClearList getNew(Matcher matcher, EventInfo info)
		{
			ISettableDataProvider<List> listDP = info.get(List.class, matcher.group(1));
			if(listDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Clear " + listDP);
			return new ClearList(matcher.group(), listDP);
		}
	}
}
