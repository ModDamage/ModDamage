package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class WorldSwitch extends SwitchRoutine<HashSet<String>, String>
{	
	public WorldSwitch(String configString, LinkedHashMap<String, Object> switchStatements)
	{ 
		super(configString, switchStatements);
	}
	@Override
	protected String getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.world.getName();}
	@Override
	protected HashSet<String> matchCase(String switchCase){ return ModDamage.matchWorldAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerSwitch(Pattern.compile("event\\.world", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends SwitchRoutine.SwitchBuilder
	{
		@Override
		public WorldSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
		{
			return new WorldSwitch(matcher.group(), switchStatements);
		}
	}
}
