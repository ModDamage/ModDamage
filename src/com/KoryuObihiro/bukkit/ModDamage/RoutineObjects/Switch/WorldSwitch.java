package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.SwitchRoutine;

public class WorldSwitch extends SwitchRoutine<List<String>>
{	
	public WorldSwitch(String configString, LinkedHashMap<String, List<Routine>> switchStatements)
	{ 
		super(configString, switchStatements);
	}
	@Override
	protected List<String> getRelevantInfo(TargetEventInfo eventInfo){ return Arrays.asList(eventInfo.world.getName());}
	@Override
	protected List<String> matchCase(String switchCase){ return ModDamage.matchWorldAlias(switchCase);}
	
	public static void register()
	{
		SwitchRoutine.registerStatement(WorldSwitch.class, Pattern.compile("switch\\.event\\.world", Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		WorldSwitch routine = null;
		if(matcher != null && switchStatements != null)
		{
			routine = new WorldSwitch(matcher.group(), switchStatements);
			return (routine.isLoaded?routine:null);
		}
		return null;
	}
}
