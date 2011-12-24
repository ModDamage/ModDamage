package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public class EventHasProjectile extends ConditionalStatement 
{
	protected EventHasProjectile(boolean inverted)
	{
		super(inverted);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return eventInfo.type.equals(EntityReference.PROJECTILE);}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)event\\.hasprojectile", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EventHasProjectile getNew(Matcher matcher)
		{
			return new EventHasProjectile(matcher.group(1).equalsIgnoreCase("!"));
		}
	}
}
