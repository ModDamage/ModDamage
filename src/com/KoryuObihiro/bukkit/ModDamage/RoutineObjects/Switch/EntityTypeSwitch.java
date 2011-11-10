package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine.EntitySingleTraitSwitchRoutine;

public class EntityTypeSwitch extends EntitySingleTraitSwitchRoutine<ModDamageElement>
{
	public EntityTypeSwitch(String configString, EntityReference entityReference, LinkedHashMap<String, Object> switchStatements)
	{
		super(configString, switchStatements, ModDamageElement.GENERIC, entityReference);
	}
	@Override
	protected ModDamageElement getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getElement(eventInfo);}
	@Override
	protected boolean compare(ModDamageElement info_event, Collection<ModDamageElement> info_case)
	{
		for(ModDamageElement element : info_case)
			if(info_event.matchesType(element))
				return true;
		return false;
	}
	@Override
	protected Collection<ModDamageElement> matchCase(String switchCase){ return AliasManager.matchElementAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.type", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public EntityTypeSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
		{
			if(EntityReference.isValid(matcher.group(1)))
				return new EntityTypeSwitch(matcher.group(),  EntityReference.match(matcher.group(1)), switchStatements);
			return null;
		}
	}
}