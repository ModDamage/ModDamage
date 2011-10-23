package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Collection;
import java.util.LinkedHashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

abstract public class EntitySwitchRoutine<StorageClass extends Collection<InfoType>, InfoType> extends SwitchRoutine<StorageClass, InfoType>
{
	protected final EntityReference entityReference;
	public EntitySwitchRoutine(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchStatements) 
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
