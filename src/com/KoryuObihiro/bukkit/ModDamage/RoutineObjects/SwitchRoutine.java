package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class SwitchRoutine<InfoType> extends Routine 
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	final protected LinkedHashMap<InfoType, List<Routine>> switchStatements;
	protected final boolean isLoaded;
	public String failedCase = "";
	
	//TODO Definitely not as efficient as it could be. Refactor?
	public SwitchRoutine(LinkedHashMap<String, List<Routine>> switchStatements)
	{
		LinkedHashMap<InfoType, List<Routine>> container = new LinkedHashMap<InfoType, List<Routine>>();
		boolean failedMatch = false;
		for(String switchCase : switchStatements.keySet())
		{
			InfoType matchedCase = matchCase(switchCase);
			if(matchedCase != null)
				container.put(matchCase(switchCase), switchStatements.get(switchCase));
			else
			{
				failedCase = switchCase;
				failedMatch = true;
				break;
			}
		}
		isLoaded = !failedMatch;
		this.switchStatements = container;
	}
	
	@Override
	public void run(DamageEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		boolean matched = false;
		if(info != null)
			for(InfoType infoKey : switchStatements.keySet())
				if(info.equals(infoKey) || matched)
				{
					if(switchStatements.get(infoKey) != null)
					{
						for(Routine routine : switchStatements.get(infoKey))
							routine.run(eventInfo);
						break;
					}
					matched = true;
				}
	}

	@Override
	public void run(SpawnEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		boolean matched = false;
		if(info != null)
			for(InfoType infoKey : switchStatements.keySet())
				if(info.equals(infoKey) || matched)
				{
					if(switchStatements.get(infoKey) != null)
					{
						for(Routine routine : switchStatements.get(infoKey))
							routine.run(eventInfo);
						break;
					}
					matched = true;
				}
	}
	
	abstract protected InfoType getRelevantInfo(DamageEventInfo eventInfo);
	
	abstract protected InfoType getRelevantInfo(SpawnEventInfo eventInfo);

	abstract protected InfoType matchCase(String switchCase);
	
	public static SwitchRoutine<?> getNew(Matcher matcher, LinkedHashMap<String, List<Routine>> switchBlock)
	{
		SwitchRoutine<?> statement = null;
		for(Pattern pattern : registeredStatements.keySet())
		{
			Matcher switchMatcher = pattern.matcher(matcher.group(1));
			if(switchMatcher.matches())
			{
				Method method = registeredStatements.get(pattern);
				try 
				{
					statement = (SwitchRoutine<?>)method.invoke(null, switchMatcher, switchBlock);
				}
				catch (Exception e){ e.printStackTrace();}
				break;
			}
		}
		return statement;
	}
	
	public static void registerStatement(RoutineUtility routineUtility, Class<? extends SwitchRoutine<?>> statementClass, Pattern syntax)
	{
		routineUtility.registerSwitch(statementClass, syntax);
	}
}
