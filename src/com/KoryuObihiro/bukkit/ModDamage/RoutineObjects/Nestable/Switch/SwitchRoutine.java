package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Switch;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

abstract public class SwitchRoutine<InfoType> extends Routine 
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	public static final String statementPart = "(!" + RoutineUtility.wordPart + "(?:\\." + RoutineUtility.wordPart +")*)";
	final protected LinkedHashMap<InfoType, List<Routine>> switchStatements;
	
	public SwitchRoutine(LinkedHashMap<InfoType, List<Routine>> switchStatements)
	{
		this.switchStatements = switchStatements;
	}
	
	@Override
	public void run(DamageEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		if(info != null && switchStatements.containsKey(info))
			for(Routine calculation : switchStatements.get(info))
				calculation.run(eventInfo);
	}

	@Override
	public void run(SpawnEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		if(info != null && switchStatements.containsKey(info))
			for(Routine calculation : switchStatements.get(info))
				calculation.run(eventInfo);
	}
	
	abstract protected InfoType getRelevantInfo(DamageEventInfo eventInfo);
	
	abstract protected InfoType getRelevantInfo(SpawnEventInfo eventInfo);
	
	public static SwitchRoutine getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchBlock)
	{
		SwitchRoutine statement = null;
		for(Pattern pattern : registeredStatements.keySet())
		{
			Matcher switchMatcher = pattern.matcher(matcher.group(1));
			if(switchMatcher.matches())
			{
				Method method = registeredStatements.get(pattern);
				//get next statement
				try 
				{
					statement = (SwitchRoutine)method.invoke(null, switchMatcher, switchBlock);
				}
				catch (Exception e){ e.printStackTrace();}
				if(statement != null) return statement;
				return null;
				//get its relation to the previous statement
			}
		}
		return statement;
	}
	
	public static void registerStatement(RoutineUtility routineUtility, Class<? extends SwitchRoutine> statementClass, Pattern syntax)
	{
		routineUtility.registerSwitch(statementClass, syntax);
	}
}
