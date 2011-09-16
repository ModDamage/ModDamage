package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class PlayerWieldSwitch extends LivingEntitySwitchRoutine<List<Material>>
{
	public PlayerWieldSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, List<Routine>> switchStatements) 
	{
		super(configString, entityReference, switchStatements);
	}
	@Override
	protected List<Material> getRelevantInfo(TargetEventInfo eventInfo){ return Arrays.asList(entityReference.getMaterial(eventInfo));}
	@Override
	protected boolean compare(List<Material> info_1, List<Material> info_2){ return info_2.contains(info_1.get(0));}
	@Override
	protected List<Material> matchCase(String switchCase){ return ModDamage.matchItemAlias(switchCase);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, PlayerWieldSwitch.class, Pattern.compile("(\\w+)\\.wielding", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWieldSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new PlayerWieldSwitch(matcher.group(), EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}
}
