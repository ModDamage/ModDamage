package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class EntityCalculatedEffectRoutine extends CalculatedEffectRoutine<LivingEntity>
{
	protected final boolean forAttacker;
	protected static final String entityPart = "(entity|attacker|target)";
	public EntityCalculatedEffectRoutine(boolean forAttacker, List<Routine> calculations)
	{
		super(calculations);
		this.forAttacker = forAttacker;
	}

	@Override
	protected LivingEntity getAffectedObject(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target);}
	@Override
	protected LivingEntity getAffectedObject(SpawnEventInfo eventInfo){ return eventInfo.entity;}
}
