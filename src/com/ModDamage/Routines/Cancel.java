package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.ISettableDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class Cancel extends NestedRoutine
{
	private final ISettableDataProvider<Boolean> cancelDP;
	
	protected Cancel(String configString, ISettableDataProvider<Boolean> cancelDP)
	{
		super(configString);
		this.cancelDP = cancelDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		cancelDP.set(data, true);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("cancel", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Cancel getNew(Matcher matcher, EventInfo info)
		{
			ISettableDataProvider<Boolean> cancelDP = info.get(Boolean.class, "cancelled", false);
			if(cancelDP == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "This event cannot be cancelled.");
				return null;
			}
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Cancel");
			return new Cancel(matcher.group(), cancelDP);
		}
	}
}
