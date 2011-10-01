package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;

abstract public class LivingEntityCalculationRoutine extends EntityCalculationRoutine<LivingEntity>
{
	public LivingEntityCalculationRoutine(String configString, EntityReference entityReference, IntegerMatch match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected LivingEntity getAffectedObject(TargetEventInfo eventInfo) 
	{
		return (entityReference.getEntity(eventInfo) instanceof LivingEntity)?(LivingEntity)entityReference.getEntity(eventInfo):null;
	}
}
