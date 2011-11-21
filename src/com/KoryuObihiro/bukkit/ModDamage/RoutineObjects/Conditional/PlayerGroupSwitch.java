package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine.EntityMultipleTraitSwitchRoutine;

public class PlayerGroupSwitch extends EntityMultipleTraitSwitchRoutine<String>
{
	public PlayerGroupSwitch(String configString, LinkedHashMap<String, Object> switchStatements, EntityReference entityReference)
	{
		super(configString, switchStatements, ModDamageElement.PLAYER, entityReference);
	}
	
	@Override
	protected List<String> getRelevantInfo(TargetEventInfo eventInfo){ return entityReference.getGroups(eventInfo);}
	
	@Override
	protected Collection<String> matchCase(String switchCase){ return AliasManager.matchGroupAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("(\\w+)\\.group", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public PlayerGroupSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
		{
			if(EntityReference.isValid(matcher.group(1)))
				return new PlayerGroupSwitch(matcher.group(), switchStatements, EntityReference.match(matcher.group(1)));
			return null;
		}
	}
	
}
