package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EventEnvironment extends ConditionalStatement 
{
	protected final Environment environment;
	public EventEnvironment(boolean inverted, Environment environment)
	{
		super(inverted);
		this.environment = environment;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return eventInfo.world.getEnvironment().equals(environment);}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)event\\.environment\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EventEnvironment getNew(Matcher matcher)
		{
			Environment environment = Environment.valueOf(matcher.group(2).toUpperCase());
			if(environment != null)
				return new EventEnvironment(matcher.group(1).equalsIgnoreCase("!"), environment);
			return null;
		}
	}
}
