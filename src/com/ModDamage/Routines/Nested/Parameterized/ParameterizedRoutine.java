package com.ModDamage.Routines.Nested.Parameterized;

import java.util.LinkedHashMap;
import java.util.List;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;

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
			Routines routines = RoutineAliaser.parseRoutines(PluginConfiguration.getCaseInsensitiveValue(map, parameters[i]));
			if(routines != null)
				integers.add(DynamicInteger.getNew(routines));
			else
				encounteredError = true;
		}
		ModDamage.changeIndentation(false);
		return !encounteredError;
	}
	
	protected static final DynamicInteger getRoutineParameter(LinkedHashMap<String, Object> map, String parameter)
	{
		Object routinesObject = PluginConfiguration.getCaseInsensitiveValue(map, parameter);
		if(routinesObject != null)
		{
			Routines routines = RoutineAliaser.parseRoutines(routinesObject);
			if(routines != null)
				return DynamicInteger.getNew(routines);
		}
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Could not find expected parameter \""+parameter+"\"");
		return null;
	}

	
}
