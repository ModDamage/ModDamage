package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

public class EnvironmentSwitch extends SwitchCalculation<Environment>
{	
	public EnvironmentSwitch(LinkedHashMap<String, List<Object>> switchStatements){ super(switchStatements);}
	@Override
	protected Environment useMatcher(String key){ return CalculationUtility.matchEnvironment(key);}

	@Override
	protected Environment getRelevantInfo(DamageEventInfo eventInfo){ return eventInfo.environment;}

	@Override
	protected Environment getRelevantInfo(SpawnEventInfo eventInfo) { return eventInfo.environment;}
}
