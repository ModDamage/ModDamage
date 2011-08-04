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
	protected boolean compare(List<String> info_1, List<String> info_2){ return info_2.contains(info_1.get(0));}
	@Override
	protected List<String> matchCase(String switchCase){ return ModDamage.matchGroupAlias(switchCase);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, PlayerGroupSwitch.class, Pattern.compile(ModDamage.entityRegex + "group", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerGroupSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null)
		{
			boolean forAttacker = matcher.group(1).equalsIgnoreCase("attacker");
			return new PlayerGroupSwitch(forAttacker, switchStatements);
		}
		return null;
	}
}
