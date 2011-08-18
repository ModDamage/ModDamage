package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Switch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World.Environment;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.SwitchRoutine;

public class EnvironmentSwitch extends SwitchRoutine<Environment>
{	
	public EnvironmentSwitch(LinkedHashMap<String, List<Routine>> switchStatements){ super(switchStatements);}
	@Override
	protected Environment getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.environment;}
	@Override
	protected Environment matchCase(String switchCase){ return ModDamage.matchEnvironment(switchCase);}
	
	public static void register(ModDamage routineUtility)
	{
		SwitchRoutine.registerStatement(routineUtility, EnvironmentSwitch.class, Pattern.compile("event\\.environment", Pattern.CASE_INSENSITIVE));
	}
	
	public static EnvironmentSwitch getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		EnvironmentSwitch routine = null;
		if(matcher != null && switchStatements != null)
		{
			routine = new EnvironmentSwitch(switchStatements);
			return (routine.isLoaded?routine:null);
		}
		return null;
	}
}
