	package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Effect;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class WorldEffectCalculation extends EffectCalculation<World>
{
	public WorldEffectCalculation(List<Routine> calculations)
	{
		super(calculations);
	}

	@Override
	protected World getAffectedObject(DamageEventInfo eventInfo){ return eventInfo.world;}
	@Override
	protected World getAffectedObject(SpawnEventInfo eventInfo){ return eventInfo.world;}
}
