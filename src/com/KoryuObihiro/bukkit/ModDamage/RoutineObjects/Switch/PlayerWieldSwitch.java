package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class PlayerWieldSwitch extends EntitySwitchRoutine<HashSet<Material>, Material>
{
	public PlayerWieldSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchStatements) 
	{
		super(configString, entityReference, switchStatements);
	}
	@Override
	protected Material getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getMaterial(eventInfo);}
	@Override
	protected boolean compare(Material info_event, HashSet<Material> info_case){ return info_case.contains(info_event);}
	@Override
	protected HashSet<Material> matchCase(String switchCase){ return ModDamage.matchMaterialAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerStatement(PlayerWieldSwitch.class, Pattern.compile("switch\\.(\\w+)\\.wielding", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerWieldSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
	{
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new PlayerWieldSwitch(matcher.group(), EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}
}
