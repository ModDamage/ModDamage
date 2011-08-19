package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class SwitchRoutine<InfoType> extends Routine 
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	final protected LinkedHashMap<InfoType, List<Routine>> switchStatements;
	public final boolean isLoaded;
	public List<String> failedCases = new ArrayList<String>();
	
	//TODO Definitely not as efficient as it could be. Refactor?
	public SwitchRoutine(String configString, LinkedHashMap<String, List<Routine>> switchStatements)
	{
		super(configString);
		LinkedHashMap<InfoType, List<Routine>> container = new LinkedHashMap<InfoType, List<Routine>>();
		boolean caseFailed = false;
		for(String switchCase : switchStatements.keySet())
		{
			InfoType matchedCase = matchCase(switchCase);
			if(matchedCase != null && (matchedCase instanceof List?!((List<?>)matchedCase).isEmpty():true))
				container.put(matchCase(switchCase), switchStatements.get(switchCase));
			else
			{
				failedCases.add(switchCase);
				caseFailed = true;
			}
		}
		isLoaded = !caseFailed;
		this.switchStatements = container;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		if(info != null)
			for(InfoType infoKey : switchStatements.keySet())
				if(compare(info, infoKey))
				{
					for(Routine routine : switchStatements.get(infoKey))
						routine.run(eventInfo);
					break;
				}
	}
	
	protected boolean compare(InfoType info_1, InfoType info_2){ return info_1.equals(info_2);}
	
	abstract protected InfoType getRelevantInfo(TargetEventInfo eventInfo);

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
	
	public static void registerStatement(ModDamage routineUtility, Class<? extends SwitchRoutine<?>> statementClass, Pattern syntax)
	{
		ModDamage.registerSwitch(statementClass, syntax);
	}
}
