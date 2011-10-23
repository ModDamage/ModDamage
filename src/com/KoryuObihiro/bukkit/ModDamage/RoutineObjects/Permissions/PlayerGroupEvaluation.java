package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityConditionalStatement;

public class PlayerGroupEvaluation extends EntityConditionalStatement
{
	final HashSet<String> groups;
	public PlayerGroupEvaluation(boolean inverted, EntityReference entityReference, HashSet<String> groups)
	{  
		super(inverted, entityReference);
		this.groups = groups;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo) 
	{
		for(String group : entityReference.getGroups(eventInfo))
			if(groups.contains(group))
				return true;
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(PlayerGroupEvaluation.class, Pattern.compile("(!?)(\\w+)\\.group\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerGroupEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			HashSet<String> matchedGroups = ModDamage.matchGroupAlias(matcher.group(3));
			if(!matchedGroups.isEmpty())
				return new PlayerGroupEvaluation(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), matchedGroups);
		}
		return null;
	}
}
