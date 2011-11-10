package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class EventWorldEvaluation extends ConditionalStatement
{
	protected final Collection<String> worlds;
	public EventWorldEvaluation(boolean inverted, Collection<String> worlds)
	{
		super(inverted);
		this.worlds = worlds;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return worlds.contains(eventInfo.world.getName());}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)event\\.world\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EventWorldEvaluation getNew(Matcher matcher)
		{
			Collection<String> worlds = AliasManager.matchWorldAlias(matcher.group(2));
			if(!worlds.isEmpty())
				return new EventWorldEvaluation(matcher.group(1).equalsIgnoreCase("!"), worlds);
			return null;
		}
	}
}
