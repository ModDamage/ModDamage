package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class WorldSwitch<InfoType> extends SwitchRoutine<InfoType>
{	
	public WorldSwitch(LinkedHashMap<InfoType, List<Routine>> switchStatements){ super(switchStatements);}
}
