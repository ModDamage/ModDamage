package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

abstract public class ParameterizedRoutine extends NestedRoutine
{
	protected final List<DynamicInteger> integers;
	
	protected ParameterizedRoutine(String configString, List<DynamicInteger> integers)
	{
		super(configString);
		this.integers = integers;
	}
	
	protected static final boolean getParameters(List<DynamicInteger> integers, LinkedHashMap<String, Object> map, String...parameters)
	{
		assert(integers.size() == 0);
		ModDamage.changeIndentation(true);
		boolean encounteredError = false;
		for(int i = 0; i < parameters.length; i++)
		{
			ModDamage.addToLogRecord(OutputPreset.INFO, parameters[i] + ": ");
			List<Routine> routines = new ArrayList<Routine>();
			if(RoutineAliaser.parseRoutines(routines, map.get(PluginConfiguration.getCaseInsensitiveKey(map, parameters[i]))))
				integers.add(DynamicInteger.getNew(routines));
			else encounteredError = true;
		}
		ModDamage.changeIndentation(false);
		return !encounteredError;
	}

}
