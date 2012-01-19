package com.ModDamage.RoutineObjects.Nested.Parameterized;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.RoutineObjects.Routine;
import com.ModDamage.RoutineObjects.Nested.NestedRoutine;

public abstract class ParameterizedRoutine extends NestedRoutine
{

	protected ParameterizedRoutine(String configString){ super(configString);}
	
	protected static final boolean getRoutineParameters(List<DynamicInteger> integers, LinkedHashMap<String, Object> map, String...parameters)
	{
		assert(integers.size() == 0);
		ModDamage.changeIndentation(true);
		boolean encounteredError = false;
		for(int i = 0; i < parameters.length; i++)
		{
			ModDamage.addToLogRecord(OutputPreset.INFO, parameters[i] + ": ");
			List<Routine> routines = new ArrayList<Routine>();
			if(RoutineAliaser.parseRoutines(routines, PluginConfiguration.getCaseInsensitiveValue(map, parameters[i])))
				integers.add(DynamicInteger.getNew(routines));
			else encounteredError = true;
		}
		ModDamage.changeIndentation(false);
		return !encounteredError;
	}
	
	protected static final DynamicInteger getRoutineParameter(LinkedHashMap<String, Object> map, String parameter)
	{
		Object routinesObject = PluginConfiguration.getCaseInsensitiveValue(map, parameter);
		if(routinesObject != null)
		{
			List<Routine> routines = new ArrayList<Routine>();
			if(RoutineAliaser.parseRoutines(routines, routinesObject))
				return DynamicInteger.getNew(routines);
		}
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Could not find expected parameter \""+parameter+"\"");
		return null;
	}

	
}
