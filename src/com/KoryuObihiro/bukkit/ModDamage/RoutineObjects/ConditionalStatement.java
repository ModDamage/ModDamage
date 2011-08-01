package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class ConditionalStatement
{
	protected final boolean inverted;
	protected ConditionalStatement(boolean inverted)
	{
		this.inverted = inverted;
	}
	
	protected abstract boolean condition(TargetEventInfo eventInfo);
}
