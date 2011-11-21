package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
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
			ModDamage.addToLogRecord(OutputPreset.INFO, "Nested: Calculate");
			ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
			List<Routine> routines = new ArrayList<Routine>();
			if(!RoutineAliaser.parseRoutines(routines, nestedContent))
			{
				ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
				ModDamage.addToLogRecord(OutputPreset.INFO, "End Calculate");
				return new Calculate(matcher.group(), routines);
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: bad routines under Calculate");
			}
			return null;
		}
	}

}
