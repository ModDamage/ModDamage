	package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class PlayerCalculatedEffectRoutine extends CalculatedEffectRoutine<Player>
{
	protected final boolean forAttacker;
	protected static final String entityPart = "(entity|attacker|target)";
	public PlayerCalculatedEffectRoutine(boolean forAttacker, List<Routine> routines)
	{
		super(routines);
		this.forAttacker = forAttacker;
	}

	@Override
	protected Player getAffectedObject(DamageEventInfo eventInfo){ return ((forAttacker?eventInfo.name_attacker:eventInfo.name_target) != null)?((Player)(forAttacker?eventInfo.entity_attacker:eventInfo.entity_target)):null;}
	@Override
	protected Player getAffectedObject(SpawnEventInfo eventInfo){ return (eventInfo.entity instanceof Player)?((Player)eventInfo.entity):null;}
}
