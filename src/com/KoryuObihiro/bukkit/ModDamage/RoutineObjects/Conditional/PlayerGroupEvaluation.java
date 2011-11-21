package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class PlayerGroupEvaluation extends EntityConditionalStatement
{
	final Collection<String> groups;
	public PlayerGroupEvaluation(boolean inverted, EntityReference entityReference, Collection<String> groups)
	{  
		super(inverted, entityReference);
		this.groups = groups;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo) 
	{
		for(String group : entityReference.getGroups(eventInfo))
			if(groups.contains(group))
				return true;
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(\\w+)\\.group\\.(\\w+)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public PlayerGroupEvaluation getNew(Matcher matcher)
		{
			Collection<String> matchedGroups = AliasManager.matchGroupAlias(matcher.group(3));
			if(!matchedGroups.isEmpty())
				return new PlayerGroupEvaluation(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matchedGroups);
			return null;
		}
	}
}
