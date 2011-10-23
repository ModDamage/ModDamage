package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;

abstract public class SwitchRoutine<StorageClass extends Collection<Type>, Type> extends NestedRoutine 
{
	public static HashMap<Pattern, Method> registeredSwitchRoutines = new HashMap<Pattern, Method>();
	protected static final Pattern switchPattern = Pattern.compile("switch\\." + Routine.statementPart, Pattern.CASE_INSENSITIVE);
	
	final protected LinkedHashMap<StorageClass, List<Routine>> switchStatements;
	public final boolean isLoaded;
	public List<String> failedCases = new ArrayList<String>();
	
	//TODO Definitely not as efficient as it could be. Refactor?
	public SwitchRoutine(String configString, LinkedHashMap<String, Object> rawSwitchStatements)
	{
		super(configString);
		this.switchStatements = new LinkedHashMap<StorageClass, List<Routine>>();
		boolean caseFailed = false;
		for(String switchCase : rawSwitchStatements.keySet())
		{
			//get the case first, see if it refers to anything valid
			StorageClass matchedCase = matchCase(switchCase);
			
			ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
			ModDamage.addToLogRecord(DebugSetting.NORMAL, " case: \"" + switchCase + "\"", LoadState.SUCCESS);

			//then grab the routines
			ModDamage.indentation++;
			LoadState[] stateMachine = { LoadState.SUCCESS };
			List<Routine> routines = RoutineAliaser.parse(rawSwitchStatements.get(switchCase), stateMachine);
			ModDamage.indentation--;
			switchStatements.put(matchedCase, stateMachine[0].equals(LoadState.FAILURE)?null:routines);
			
			ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End case \"" + switchCase + "\"\n", LoadState.SUCCESS);
			
			//Check if the case is valid
			if(!(matchedCase != null && !matchedCase.isEmpty() && switchStatements.get(matchedCase) != null ))
			{
				failedCases.add(switchCase);
				caseFailed = true;
			}
		}
		isLoaded = !caseFailed;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		Type info = getRelevantInfo(eventInfo);
		if(info != null)
			for(StorageClass infoKey : switchStatements.keySet())
				if(compare(info, infoKey))
				{
					for(Routine routine : switchStatements.get(infoKey))
						routine.run(eventInfo);
					break;
				}
	}
	
	protected boolean compare(Type info_event, StorageClass info_case){ return info_event.equals(info_case);}
	
	abstract protected Type getRelevantInfo(TargetEventInfo eventInfo);

	abstract protected StorageClass matchCase(String switchCase);
	
	public static SwitchRoutine<?, ?> getNew(String string, Object nestedContent)
	{
		if(string != null && nestedContent != null)
		{
			ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
			ModDamage.addToLogRecord(DebugSetting.NORMAL, "Switch: \"" + string + "\"", LoadState.SUCCESS);
			for(Pattern pattern : registeredSwitchRoutines.keySet())
			{
				Matcher matcher = pattern.matcher(string);
				if(matcher.matches())
				{
					@SuppressWarnings("unchecked")
					LinkedHashMap<String, Object> switchCases = (nestedContent instanceof LinkedHashMap?(LinkedHashMap<String, Object>)nestedContent:null);
					if(switchCases != null)
					{
						SwitchRoutine<?, ?> routine = null;
						try
						{
							routine = (SwitchRoutine<?, ?>)registeredSwitchRoutines.get(pattern).invoke(null, matcher, switchCases);
						}
						catch (Exception e){ e.printStackTrace();}
						if(routine != null)
						{
							if(routine.isLoaded)
							{
								ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Switch \"" + string + "\"", LoadState.SUCCESS);
								return routine;
							}
							else 
								for(String caseName : routine.failedCases)
									ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: invalid case \"" + caseName + "\"", LoadState.FAILURE);
						}
					}
					else ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: unexpected nested content " + nestedContent.toString() + " in Switch routine \"" + string + "\"", LoadState.FAILURE);
					break;
				}
			}
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: invalid Switch \"" + string + "\"", LoadState.FAILURE);
		}
		return null;
	}
	
	public static void registerStatement(Class<? extends SwitchRoutine<?, ?>> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", Matcher.class, LinkedHashMap.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (Matcher)null, (LinkedHashMap<String, List<Routine>>)null);
				Routine.register(SwitchRoutine.registeredSwitchRoutines, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher, LinkedHashMap)!");} 
		catch (IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}
	
	public static void register()
	{
		NestedRoutine.registerNested(SwitchRoutine.class, Pattern.compile("switch\\..*", Pattern.CASE_INSENSITIVE));
	}
}
