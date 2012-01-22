package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Routines;

public final class ChangeProperty extends NestedRoutine
{	
	final Routines routines;
	protected final DynamicInteger targetPropertyMatch;
	public ChangeProperty(String configString, Routines routines, DynamicInteger targetPropertyMatch)
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
		routines.run(eventInfo);
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
			Routines routines = RoutineAliaser.parseRoutines(nestedContent);
			if(targetPropertyMatch != null && routines != null)
			{
				if(targetPropertyMatch.isSettable())
					return new ChangeProperty(matcher.group(), routines, targetPropertyMatch);
				else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Variable \"" + matcher.group(1) + "\" is read-only.");
			}
			return null;
		}
	}
}
