package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

public class EntityTargetedByOther extends EntityConditionalStatement<LivingEntity>
{
	LivingEntity value;
	public EntityTargetedByOther(boolean inverted, boolean forAttacker)
	{ 
		super(inverted, forAttacker, null);
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo)
	{ 
		value = (forAttacker?eventInfo.entity_target:eventInfo.entity_attacker);
		return ((Creature)getRelevantEntity(eventInfo)).getTarget().equals(value);
	}
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
	
	@Override
	protected LivingEntity getRelevantInfo(DamageEventInfo eventInfo){ return null;}
	@Override
	protected LivingEntity getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	
	public static void register(RoutineUtility routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityTargetedByOther.class, Pattern.compile(RoutineUtility.entityPart + "targetedbyother", Pattern.CASE_INSENSITIVE));
	}
}
