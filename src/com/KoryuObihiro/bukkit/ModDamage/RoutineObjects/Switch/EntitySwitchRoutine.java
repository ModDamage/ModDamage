package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

abstract public class EntitySwitchRoutine<InfoType> extends SwitchRoutine<InfoType>
{
	protected final boolean forAttacker;
	public EntitySwitchRoutine(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchStatements) 
	{
		super(switchStatements);
		this.forAttacker = forAttacker;
	}
}
