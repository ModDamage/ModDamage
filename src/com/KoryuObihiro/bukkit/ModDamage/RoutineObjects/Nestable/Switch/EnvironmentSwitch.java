package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EnvironmentSwitch extends WorldSwitch<Environment>
{	
	public EnvironmentSwitch(LinkedHashMap<Environment, List<Routine>> switchStatements){ super(switchStatements);}

	@Override
	protected Environment getRelevantInfo(DamageEventInfo eventInfo){ return eventInfo.environment;}

	@Override
	protected Environment getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.environment;}	
}
