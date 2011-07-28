package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class ConditionalStatement
{
	protected final boolean inverted;
	protected ConditionalStatement(boolean inverted)
	{
		this.inverted = inverted;
	}
	
	protected abstract boolean condition(DamageEventInfo eventInfo);
	protected abstract boolean condition(SpawnEventInfo eventInfo);
}
