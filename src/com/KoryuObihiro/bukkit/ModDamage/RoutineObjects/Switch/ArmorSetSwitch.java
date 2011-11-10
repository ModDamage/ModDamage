package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.ArmorSet;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine.EntitySingleTraitSwitchRoutine;

public class ArmorSetSwitch extends EntitySingleTraitSwitchRoutine<ArmorSet>
{
	public ArmorSetSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchStatements)
	{ 
		super(configString, switchStatements, ModDamageElement.PLAYER, entityReference);
	}

	@Override
	protected ArmorSet getRelevantInfo(TargetEventInfo eventInfo)
	{ 
		return entityReference.getArmorSet(eventInfo);
	}
	@Override
	protected boolean compare(ArmorSet info_event, Collection<ArmorSet> info_case)
	{
		for(ArmorSet armorSet : info_case)
			if(armorSet.equals(info_event))
				return true;
		return false;
	}
	@Override
	protected Collection<ArmorSet> matchCase(String switchCase)
	{ 
		Collection<ArmorSet> armorSet = AliasManager.matchArmorAlias(switchCase);
		return (armorSet.isEmpty()?null:armorSet);
	}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.wearing(only)?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
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
