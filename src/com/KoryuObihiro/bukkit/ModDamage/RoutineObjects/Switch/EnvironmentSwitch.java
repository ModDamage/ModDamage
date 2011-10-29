package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class EnvironmentSwitch extends SwitchRoutine<HashSet<Environment>, Environment>
{	
	public EnvironmentSwitch(String configString, LinkedHashMap<String, Object> switchStatements){ super(configString, switchStatements);}
	@Override
	protected Environment getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.world.getEnvironment();}
	@Override
	protected HashSet<Environment> matchCase(String switchCase){ return new HashSet<Environment>(Arrays.asList(ModDamage.matchEnvironment(switchCase)));}
	
	public static void register()
	{
		SwitchRoutine.registerStatement(EnvironmentSwitch.class, Pattern.compile("event\\.environment", Pattern.CASE_INSENSITIVE));
	}
	
	public static EnvironmentSwitch getNew(Matcher matcher, LinkedHashMap<String, Object> switchStatements)
	{
		if(matcher != null && switchStatements != null)
			return new EnvironmentSwitch(matcher.group(), switchStatements);
		return null;
	}
}