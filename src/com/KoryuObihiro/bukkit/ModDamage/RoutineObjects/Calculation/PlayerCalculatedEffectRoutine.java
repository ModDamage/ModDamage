package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class PlayerCalculatedEffectRoutine extends CalculationRoutine<Player>
{
	protected final boolean forAttacker;
	protected static final String entityPart = "(entity|attacker|target)";
	public PlayerCalculatedEffectRoutine(String configString, boolean forAttacker, List<Routine> routines)
	{
		super(configString, routines);
		this.forAttacker = forAttacker;
	}
	@Override
	protected Player getAffectedObject(TargetEventInfo eventInfo){ return (eventInfo.getRelevantEntity(forAttacker) instanceof Player?(Player)eventInfo.getRelevantEntity(forAttacker):null);}
}