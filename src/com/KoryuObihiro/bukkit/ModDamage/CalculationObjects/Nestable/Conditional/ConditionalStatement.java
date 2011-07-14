package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class ConditionalStatement
{
	final boolean inverted;
	ConditionalStatement(boolean inverted)
	{
		this.inverted = inverted;
	}
	
	abstract public boolean condition(DamageEventInfo eventInfo);
	abstract public boolean condition(SpawnEventInfo eventInfo);
}
