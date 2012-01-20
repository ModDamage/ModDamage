package com.ModDamage.RoutineObjects.Base;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.AliasManager;
import com.ModDamage.RoutineObjects.Routine;

public class AliasedRoutine extends Routine
{
	final String alias;
	
	public AliasedRoutine(String configString, String alias)
	{
		super(configString);
		this.alias = alias;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		Collection<Routine> aliasedRoutines = AliasManager.matchRoutineAlias(alias);
		if (aliasedRoutines != null)
			for (Routine routine : aliasedRoutines)
				routine.run(eventInfo);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("_\\w+", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public AliasedRoutine getNew(Matcher matcher)
		{
			String alias = matcher.group();
			Collection<Routine> aliasedRoutines = AliasManager.matchRoutineAlias(alias);
			if(aliasedRoutines != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Routine Alias: \"" + alias + "\"");
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid routine alias: \"" + alias + "\"");
				return null;
			}
			return new AliasedRoutine(matcher.group(), matcher.group());
		}
	}

}
