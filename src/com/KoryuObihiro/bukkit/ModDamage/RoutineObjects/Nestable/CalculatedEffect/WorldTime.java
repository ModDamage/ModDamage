	package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.CalculatedEffect;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class WorldTime extends WorldCalculatedEffectRoutine
{
	public WorldTime(List<Routine> calculations){ super(calculations);}

	@Override
	protected World getAffectedObject(DamageEventInfo eventInfo){ return eventInfo.world;}
	@Override
	protected World getAffectedObject(SpawnEventInfo eventInfo){ return eventInfo.world;}

	@Override
	void applyEffect(World affectedObject, int input){ affectedObject.setFullTime(input);}
}
