package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

abstract public class LivingEntitySwitchRoutine<InfoType> extends SwitchRoutine<InfoType>
{
	protected final EntityReference entityReference;
	public LivingEntitySwitchRoutine(String configString, EntityReference entityReference, LinkedHashMap<String, List<Routine>> switchStatements) 
	{
		super(configString, switchStatements);
		this.entityReference = entityReference;
	}

	protected LivingEntity getRelevantEntity(TargetEventInfo eventInfo)
	{
		Entity entity =  entityReference.getEntity(eventInfo);
		return (entityReference.getElement(eventInfo).matchesType(ModDamageElement.LIVING))?(LivingEntity)entity:null;
	}
}
