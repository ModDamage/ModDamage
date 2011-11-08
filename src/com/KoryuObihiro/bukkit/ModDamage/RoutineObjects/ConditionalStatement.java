package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.regex.Matcher;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class ConditionalStatement
{
	protected final boolean inverted;
	protected ConditionalStatement(boolean inverted)
	{
		this.inverted = inverted;
	}
	
	abstract protected boolean condition(TargetEventInfo eventInfo);

	abstract protected static class StatementBuilder
	{
		abstract public ConditionalStatement getNew(Matcher matcher);
	}
}
