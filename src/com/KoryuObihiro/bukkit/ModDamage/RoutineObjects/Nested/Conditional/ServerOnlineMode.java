package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public class ServerOnlineMode extends ConditionalStatement
{
	public ServerOnlineMode(boolean inverted)
	{
		super(inverted);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return Bukkit.getOnlineMode();}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)server\\.onlineMode", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public ServerOnlineMode getNew(Matcher matcher)
		{
			return new ServerOnlineMode(matcher.group(1).equalsIgnoreCase("!"));
		}
	}
}
