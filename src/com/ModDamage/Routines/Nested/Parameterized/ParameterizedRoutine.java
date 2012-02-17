package com.ModDamage.Routines.Nested.Parameterized;

import java.util.LinkedHashMap;
import java.util.List;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;

public abstract class ParameterizedRoutine extends NestedRoutine
{

	protected ParameterizedRoutine(String configString){ super(configString); }
	
	protected static final boolean getRoutineParameters(List<DynamicInteger> integers, LinkedHashMap<String, Object> map, EventInfo info, String...parameters)
	{
		assert(integers.size() == 0);
		ModDamage.changeIndentation(true);
		boolean encounteredError = false;
		for(String parameter : parameters)
		{
			EventInfo myInfo = new SimpleEventInfo(IntRef.class, parameter.toLowerCase(), "-default");
			EventInfo einfo = info.chain(myInfo);
			ModDamage.addToLogRecord(OutputPreset.INFO, parameter + ": ");
			Routines routines = RoutineAliaser.parseRoutines(PluginConfiguration.getCaseInsensitiveValue(map, parameter), einfo);
			if(routines != null)
				integers.add(DynamicInteger.getNew(routines, einfo));
			else
				encounteredError = true;
		}
		ModDamage.changeIndentation(false);
		return !encounteredError;
	}
	
	/*protected static final DynamicInteger getRoutineParameter(LinkedHashMap<String, Object> map, EventInfo info, String parameter)
	{
		Object routinesObject = PluginConfiguration.getCaseInsensitiveValue(map, parameter);
		if(routinesObject != null)
		{
			Routines routines = RoutineAliaser.parseRoutines(routinesObject, info);
			if(routines != null)
				return DynamicInteger.getNew(routines, info);
		}
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Could not find expected parameter \""+parameter+"\"");
		return null;
	}*/

	
}
