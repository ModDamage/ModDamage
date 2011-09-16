package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;

import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class PlayerCalculationRoutine extends EntityCalculationRoutine<Player>
{
	public PlayerCalculationRoutine(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, entityReference, routines);
	}
	@Override
	protected Player getAffectedObject(TargetEventInfo eventInfo)
	{
		return (entityReference.getEntity(eventInfo) instanceof Player?(Player)entityReference.getEntity(eventInfo):null);
	}
}
