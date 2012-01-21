package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Routine;

public final class ChangeProperty extends NestedRoutine
{	
	final List<Routine> routines;
	protected final DynamicInteger targetPropertyMatch;
	public ChangeProperty(String configString, List<Routine> routines, DynamicInteger targetPropertyMatch)
	{
		super(configString);
		this.routines = routines;
		this.targetPropertyMatch = targetPropertyMatch;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		int savedEventValue = eventInfo.eventValue;
		
		eventInfo.eventValue = targetPropertyMatch.getValue(eventInfo);
		for(Routine routine : routines)
			routine.run(eventInfo);
		targetPropertyMatch.setValue(eventInfo, eventInfo.eventValue);
		
		eventInfo.eventValue = savedEventValue;
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("set\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public ChangeProperty getNew(Matcher matcher, Object nestedContent)
		{
			DynamicInteger targetPropertyMatch = DynamicInteger.getNew(matcher.group(1));
			List<Routine> routines = new ArrayList<Routine>();
			if(targetPropertyMatch != null && RoutineAliaser.parseRoutines(routines, nestedContent))
			{
				if(targetPropertyMatch.isSettable())
					return new ChangeProperty(matcher.group(), routines, targetPropertyMatch);
				else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Variable \"" + matcher.group(1) + "\" is read-only.");
			}
			return null;
		}
	}
}
