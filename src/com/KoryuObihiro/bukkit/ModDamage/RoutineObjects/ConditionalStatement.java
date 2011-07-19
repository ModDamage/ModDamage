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
	
	abstract public boolean condition(DamageEventInfo eventInfo);
	abstract public boolean condition(SpawnEventInfo eventInfo);
}
