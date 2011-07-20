	package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

abstract public class WorldCalculatedEffectRoutine extends CalculatedEffectRoutine<World>
{
	public WorldCalculatedEffectRoutine(List<Routine> routines)
	{
		super(routines);
	}

	@Override
	protected World getAffectedObject(DamageEventInfo eventInfo){ return eventInfo.world;}
	@Override
	protected World getAffectedObject(SpawnEventInfo eventInfo){ return eventInfo.world;}
}
