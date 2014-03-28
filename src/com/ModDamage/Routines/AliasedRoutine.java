package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.LogUtil;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class AliasedRoutine extends Routine
{
	private Routines routines;
	
	public AliasedRoutine(final ScriptLine scriptLine, final EventInfo info, final String alias)
	{
		super(scriptLine);
		
		// fetch after, to avoid infinite recursion
		RoutineAliaser.whenDoneParsingAlias(new Runnable() {
				@Override public void run() {
					routines = RoutineAliaser.match(scriptLine, alias, info);
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
		Routine.registerRoutine(Pattern.compile("_\\w+", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			String alias = matcher.group();
			/*Routines aliasedRoutines = RoutineAliaser.match(alias, info);
			if(aliasedRoutines != null)
			{
				LogUtil.info("Routine Alias: \"" + alias + "\"");
			}
			else
			{
				LogUtil.error("Invalid routine alias: \"" + alias + "\"");
				return null;
			}*/
			LogUtil.info("Routine Alias: \"" + alias + "\"");
			return new RoutineBuilder(new AliasedRoutine(scriptLine, info, alias));
		}
	}

}
