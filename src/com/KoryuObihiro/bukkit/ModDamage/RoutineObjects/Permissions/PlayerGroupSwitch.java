package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.LivingEntitySwitchRoutine;

public class PlayerGroupSwitch extends LivingEntitySwitchRoutine<List<String>>
{
	public PlayerGroupSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, List<Routine>> switchLabels)
	{
		super(configString, entityReference, switchLabels);
	}
	@Override
	protected List<String> getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getGroups(eventInfo);}
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
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new PlayerGroupSwitch(matcher.group(),  EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}
}
