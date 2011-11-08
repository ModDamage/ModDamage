package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo.EventInfoType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EventHasRangedElement extends ConditionalStatement 
{
	protected EventHasRangedElement(boolean inverted)
	{
		super(inverted);
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return eventInfo.type.equals(EventInfoType.PROJECTILE);}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)event\\.hasrangedelement", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EventHasRangedElement getNew(Matcher matcher)
		{	
			return new EventHasRangedElement(matcher.group(1).equalsIgnoreCase("!"));
		}
	}
}
