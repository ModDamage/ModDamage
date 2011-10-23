package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch.EntitySwitchRoutine;

public class PlayerGroupSwitch extends EntitySwitchRoutine<HashSet<String>, String>
{
	public PlayerGroupSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchLabels)
	{
		super(configString, entityReference, switchLabels);
	}
	
	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		List<String> playerGroups = entityReference.getGroups(eventInfo);
		for(HashSet<String> caseGroups : switchStatements.keySet())
			for(String group : playerGroups)
				if(caseGroups.contains(group))
				{
					for(Routine routine : switchStatements.get(caseGroups))
						routine.run(eventInfo);
					break;
				}
	}
	
	@Override
	protected String getRelevantInfo(TargetEventInfo eventInfo){ return null;}
	
	@Override
	protected boolean compare(String info_event, HashSet<String> info_case){ return false;}
	
	@Override
	protected HashSet<String> matchCase(String switchCase){ return ModDamage.matchGroupAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerStatement(PlayerGroupSwitch.class, Pattern.compile("(\\w+)\\.group", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerGroupSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
	{
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new PlayerGroupSwitch(matcher.group(),  EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}
}
