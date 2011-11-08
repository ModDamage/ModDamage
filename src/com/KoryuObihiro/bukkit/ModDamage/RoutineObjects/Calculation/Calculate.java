package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Calculate extends NestedRoutine
{
	final List<Routine> routines;
	protected Calculate(String configString, List<Routine> routines)
	{
		super(configString);
		this.routines = routines;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		int value = eventInfo.eventValue;
		for(Routine routine : routines)
			routine.run(eventInfo);
		eventInfo.eventValue = value;
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("calculate", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public Calculate getNew(Matcher matcher, Object nestedContent)
		{
			ModDamage.addToLogRecord(DebugSetting.NORMAL, "Nested: Calculate", LoadState.SUCCESS);
			ModDamage.indentation++;
			LoadState[] stateMachine = { LoadState.SUCCESS };
			List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
			ModDamage.indentation--;
			if(stateMachine[0].equals(LoadState.SUCCESS))
			{
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "End Calculate", LoadState.SUCCESS);
				return new Calculate(matcher.group(), routines);
			}
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: bad routines under Calculate", LoadState.SUCCESS);
			return null;
		}
	}

}
