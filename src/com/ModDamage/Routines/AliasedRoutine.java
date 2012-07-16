package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class AliasedRoutine extends Routine
{
	private Routines routines;
	
	public AliasedRoutine(String configString, final EventInfo info, final String alias)
	{
		super(configString);
		
		// fetch after, to avoid infinite recursion
		RoutineAliaser.whenDoneParsingAlias(new Runnable() {
				@Override public void run() {
					routines = RoutineAliaser.match(alias, info);
				}
			});
	}

	@Override
	public void run(EventData data) throws BailException
	{
		if (routines != null)
			routines.run(data);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("_\\w+", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public AliasedRoutine getNew(Matcher matcher, EventInfo info)
		{
			String alias = matcher.group();
			/*Routines aliasedRoutines = RoutineAliaser.match(alias, info);
			if(aliasedRoutines != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Routine Alias: \"" + alias + "\"");
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid routine alias: \"" + alias + "\"");
				return null;
			}*/
			ModDamage.addToLogRecord(OutputPreset.INFO, "Routine Alias: \"" + alias + "\"");
			return new AliasedRoutine(matcher.group(), info, alias);
		}
	}

}
