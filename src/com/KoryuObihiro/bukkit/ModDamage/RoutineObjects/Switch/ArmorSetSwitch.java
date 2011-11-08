package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class ArmorSetSwitch extends EntitySwitchRoutine<List<ArmorSet>, ArmorSet>
{
	public ArmorSetSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchStatements)
	{ 
		super(configString, entityReference, switchStatements);
	}

	@Override
	protected ArmorSet getRelevantInfo(TargetEventInfo eventInfo)
	{ 
		return entityReference.getArmorSet(eventInfo);
	}
	@Override
	protected boolean compare(ArmorSet info_event, List<ArmorSet> info_case)
	{ 
		for(ArmorSet armorSet : info_case)
			if(armorSet.equals(info_event))
				return true;
		return false;
	}
	@Override
	protected List<ArmorSet> matchCase(String switchCase)
	{ 
		List<ArmorSet> armorSet = ModDamage.matchArmorAlias(switchCase);
		return (armorSet.isEmpty()?null:armorSet);
	}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.armorset", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public ArmorSetSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
		{
			if(EntityReference.isValid(matcher.group(1)))
				return new ArmorSetSwitch(matcher.group(), EntityReference.match(matcher.group(1)), switchStatements);
			return null;
		}
	}
}
