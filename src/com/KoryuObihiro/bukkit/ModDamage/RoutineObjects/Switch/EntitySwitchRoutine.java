package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
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

	protected LivingEntity getRelevantEntity(TargetEventInfo eventInfo){ return (forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).entity_attacker:eventInfo.entity_target;}
}
