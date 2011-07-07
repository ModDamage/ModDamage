package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class SwitchCalculation<T> implements ModDamageCalculation 
{
	final protected HashMap<T, List<ModDamageCalculation>> switchLabels;
	
	public SwitchCalculation(LinkedHashMap<String, List<Object>> switchStatements)
	{

	}
	
	@Override
	public void calculate(DamageEventInfo eventInfo) 
	{
		T t = getRelevantInfo(eventInfo);
		if(t != null && switchLabels.containsKey(t))
			for(ModDamageCalculation calculation : switchLabels.get(t))
				calculation.calculate(eventInfo);
	}

	@Override
	public void calculate(SpawnEventInfo eventInfo) 
	{
		T t = getRelevantInfo(eventInfo);
		if(t != null && switchLabels.containsKey(t))
			for(ModDamageCalculation calculation : switchLabels.get(t))
				calculation.calculate(eventInfo);
	}

	abstract protected T getRelevantInfo(DamageEventInfo eventInfo);
	abstract protected T getRelevantInfo(SpawnEventInfo eventInfo);
	
	abstract protected T useMatcher(String key);
}
