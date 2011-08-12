package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class PlayerGroupEvaluation extends EntityConditionalStatement<List<String>>
{
	public PlayerGroupEvaluation(boolean inverted, boolean forAttacker, List<String> value)
	{  
		super(inverted, forAttacker, value);
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo) 
	{
		for(String group : getRelevantInfo(eventInfo))
			if(value.contains(group))
				return true;
		return false;
	}
	@Override
	protected List<String> getRelevantInfo(TargetEventInfo eventInfo){ return shouldGetAttacker(eventInfo)?((AttackerEventInfo)eventInfo).groups_attacker:eventInfo.groups_target;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, PlayerGroupEvaluation.class, Pattern.compile("(!)?" + ModDamage.entityRegex + "group\\.(" + ModDamage.nonAliasPart + ")", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerGroupEvaluation getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			List<String> matchedGroups = ModDamage.matchGroupAlias(matcher.group(3));
			if(!matchedGroups.isEmpty())
				return new PlayerGroupEvaluation(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), matchedGroups);
		}
		return null;
	}
}
