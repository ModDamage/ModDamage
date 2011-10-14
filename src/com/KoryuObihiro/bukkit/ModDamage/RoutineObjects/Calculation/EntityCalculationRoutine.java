package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

abstract public class EntityCalculationRoutine<T extends Entity> extends CalculationRoutine<T>
{
	protected final EntityReference entityReference;
	public EntityCalculationRoutine(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, match);
		this.entityReference = entityReference;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected T getAffectedObject(TargetEventInfo eventInfo){ return (T) entityReference.getEntity(eventInfo);}
}