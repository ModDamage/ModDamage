package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class PlayerGroupSwitch extends EntitySwitchRoutine<List<String>>
{
	public PlayerGroupSwitch(boolean forAttacker, LinkedHashMap<String, List<Routine>> switchLabels)
	{
		super(forAttacker, switchLabels);
	}
	@Override
	protected List<String> getRelevantInfo(TargetEventInfo eventInfo){ return (forAttacker && eventInfo instanceof AttackerEventInfo)?((AttackerEventInfo)eventInfo).groups_attacker:eventInfo.groups_target;}
	@Override
	protected boolean compare(List<String> playerGroups, List<String> caseGroups)
	{ 
		for(String group : playerGroups)
			if(caseGroups.contains(group))
				return true;
		return false;
	}
	@Override
	protected List<String> matchCase(String switchCase){ return ModDamage.matchGroupAlias(switchCase);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, PlayerGroupSwitch.class, Pattern.compile("(\\w+)\\.group", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerGroupSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = (ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false;
			return new PlayerGroupSwitch(forAttacker, switchStatements);
		}
		return null;
	}
}
