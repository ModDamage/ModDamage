package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class EntityCalculatedEffectRoutine extends CalculatedEffectRoutine<LivingEntity>
{
	protected final boolean forAttacker;
	protected static final String entityPart = "(entity|attacker|target)";
	public EntityCalculatedEffectRoutine(boolean forAttacker, List<Routine> routines)
	{
		super(routines);
		this.forAttacker = forAttacker;
	}
	@Override
	protected LivingEntity getAffectedObject(TargetEventInfo eventInfo){ return (forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).entity_attacker:eventInfo.entity_target;}
}
