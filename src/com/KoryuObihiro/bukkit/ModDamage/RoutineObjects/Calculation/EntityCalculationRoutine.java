package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

abstract public class EntityCalculationRoutine<T extends Entity> extends CalculationRoutine<T>
{
	protected final EntityReference entityReference;
	public EntityCalculationRoutine(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, routines);
		this.entityReference = entityReference;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected T getAffectedObject(TargetEventInfo eventInfo){ return (T) entityReference.getEntity(eventInfo);}
}