	package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Effect;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class EntityEffectCalculation extends EffectCalculation<LivingEntity>
{
	protected final boolean forAttacker;
	public EntityEffectCalculation(boolean forAttacker, List<Routine> calculations)
	{
		super(calculations);
		this.forAttacker = forAttacker;
	}

	@Override
	protected LivingEntity getAffectedObject(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target);}
	@Override
	protected LivingEntity getAffectedObject(SpawnEventInfo eventInfo){ return eventInfo.entity;}
	
}
