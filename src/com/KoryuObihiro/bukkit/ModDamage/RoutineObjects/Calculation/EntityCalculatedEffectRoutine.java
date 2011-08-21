package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class EntityCalculatedEffectRoutine extends CalculationRoutine<LivingEntity>
{
	protected final boolean forAttacker;
	public EntityCalculatedEffectRoutine(boolean forAttacker, List<Routine> routines)
	{
		super(routines);
		this.forAttacker = forAttacker;
	}
	@Override
	protected LivingEntity getAffectedObject(TargetEventInfo eventInfo){ return (forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).entity_attacker:eventInfo.entity_target;}
}
