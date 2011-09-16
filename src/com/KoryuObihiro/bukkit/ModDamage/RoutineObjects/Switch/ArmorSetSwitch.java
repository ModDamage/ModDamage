package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class ArmorSetSwitch extends LivingEntitySwitchRoutine<List<ArmorSet>>
{
	public ArmorSetSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, List<Routine>> switchStatements)
	{ 
		super(configString, entityReference, switchStatements);
	}

	@Override
	protected List<ArmorSet> getRelevantInfo(TargetEventInfo eventInfo)
	{ 
		return Arrays.asList(entityReference.getArmorSet(eventInfo));
	}
	@Override
	protected boolean compare(List<ArmorSet> info_1, List<ArmorSet> info_2)
	{ 
		for(ArmorSet armorSet : info_2)
			if(armorSet.equals(info_1.get(0)))
				return true;
		return false;
	}
	@Override
	protected List<ArmorSet> matchCase(String switchCase)
	{ 
		List<ArmorSet> armorSet = ModDamage.matchArmorAlias(switchCase);
		return (armorSet.isEmpty()?null:armorSet);
	}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, ArmorSetSwitch.class, Pattern.compile("(\\w+)\\.armorset", Pattern.CASE_INSENSITIVE));
	}
	
	public static ArmorSetSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		if(matcher != null && switchStatements != null && EntityReference.isValid(matcher.group(1)))
			return new ArmorSetSwitch(matcher.group(), EntityReference.match(matcher.group(1)), switchStatements);
		return null;
	}

}
