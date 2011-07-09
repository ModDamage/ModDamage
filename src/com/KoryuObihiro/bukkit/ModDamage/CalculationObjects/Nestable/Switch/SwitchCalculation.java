package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.HashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class SwitchCalculation<InfoType> extends ModDamageCalculation 
{
	final protected HashMap<InfoType, List<ModDamageCalculation>> switchStatements;
	
	public SwitchCalculation(HashMap<InfoType, List<ModDamageCalculation>> switchStatements)
	{
		this.switchStatements = switchStatements;
	}
	
	@Override
	public void calculate(DamageEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		if(info != null && switchStatements.containsKey(info))
			for(ModDamageCalculation calculation : switchStatements.get(info))
				calculation.calculate(eventInfo);
	}

	@Override
	public void calculate(SpawnEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		if(info != null && switchStatements.containsKey(info))
			for(ModDamageCalculation calculation : switchStatements.get(info))
				calculation.calculate(eventInfo);
	}
	
	abstract protected InfoType getRelevantInfo(DamageEventInfo eventInfo);
	
	abstract protected InfoType getRelevantInfo(SpawnEventInfo eventInfo);
}
