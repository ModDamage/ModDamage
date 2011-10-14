package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class RoutineAliaser extends Aliaser<Routine> 
{
	private static final long serialVersionUID = -2744471820826321788L;
	public RoutineAliaser(){ super("Routine");}
	
	public boolean addAlias(String key, List<Object> values)
	{
		if(this.containsKey(key)) return false;
		ModDamage.addToLogRecord(DebugSetting.NORMAL, "Adding " + name + " alias \"" + key + "\"", LoadState.SUCCESS);
		
		ModDamage.indentation++;
		if(values.toString().contains("_" + key))
		{
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Error adding value \"_" + key + "\" - value is self-referential!", loadState);
			return false;
		}
		LoadState[] addStateMachine = {LoadState.SUCCESS};
		
		ModDamage.indentation++;
		List<Routine> matchedItems = parse(values, addStateMachine);
		ModDamage.indentation--;
		
		if(!addStateMachine[0].equals(LoadState.SUCCESS))
		{
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Error adding value " + values.toString(), loadState);
			return false;
		}
		ModDamage.indentation--;
		
		this.put("_" + key, matchedItems);
		return true;
	}

	@Override
	public List<Routine> matchAlias(String key)
	{
		if(this.containsKey(key)) return this.get(key);
		return new ArrayList<Routine>();
	}
	
	@Override
	@Deprecated
	protected Routine matchNonAlias(String key){ return null;}
	
	//Parse routine strings recursively
	@SuppressWarnings("unchecked")
	public static List<Routine> parse(Object object, LoadState[] resultingState)
	{
		LoadState currentState = LoadState.SUCCESS;
		List<Routine> routines = new ArrayList<Routine>();
		if(object != null)
		{
			if(object instanceof String)
			{
				if(((String)object).startsWith("_"))
				{
					List<Routine> aliasedRoutines = ModDamage.matchRoutineAlias((String)object);
					if(!aliasedRoutines.isEmpty())
					{
						ModDamage.addToLogRecord(DebugSetting.NORMAL, "Alias: \"" + ((String)object).substring(1) + "\"", LoadState.SUCCESS);
						routines.addAll(aliasedRoutines);
					}
					else ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid routine alias " + ((String)object).substring(1), LoadState.FAILURE);
				}
				if(routines.isEmpty())
				{
					Routine routine = null;
					for(Pattern pattern : Routine.registeredBaseRoutines.keySet())
					{
						Matcher matcher = pattern.matcher((String)object);
						if(matcher.matches())
						{
							try
							{
								routine = (Routine)Routine.registeredBaseRoutines.get(pattern).invoke(null, matcher);
								if(routine != null)
								{
									routines.add(routine);
									ModDamage.addToLogRecord(DebugSetting.NORMAL, "Routine: \"" + (String)object + "\"", currentState);
								}
								else
								{
									//TODO: Catch what routine matched, if/when it failed.
									currentState = LoadState.FAILURE;
									ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Bad parameters for new " + Routine.registeredBaseRoutines.get(pattern).getClass().getSimpleName() + " \"" + (String)object + "\"", currentState);
								}
								break;
							}
							catch(Exception e){ e.printStackTrace();}
						}
					}
				}
				if(routines.isEmpty())
				{
					currentState = LoadState.FAILURE;
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid base routine " + " \"" + (String)object + "\"", currentState);
				}
			}
			else if(object instanceof LinkedHashMap)
			{
				LinkedHashMap<String, Object> someHashMap = (LinkedHashMap<String, Object>)object;
				if(someHashMap.keySet().size() == 1)
					for(String key : someHashMap.keySet())//A properly-formatted nested routine is a LinkedHashMap with only one key.
					{
						Object nestedContent = someHashMap.get(key);
						NestedRoutine routine = NestedRoutine.getNew(key, nestedContent);
						if(routine != null)
							routines.add(routine);
						else currentState = LoadState.FAILURE;
					}
				else
				{
					String[] keys = (String[]) someHashMap.keySet().toArray();
					for(int i = 0; i < keys.length; i++)
						ModDamage.addToLogRecord(DebugSetting.QUIET, "Parse error: bad nested routine \"" + keys[i] + "\"", LoadState.FAILURE);
					currentState = LoadState.FAILURE;			
				}
			}
			else if(object instanceof List)
				for(Object nestedObject : (List<Object>)object)
				{
					LoadState[] stateMachine = { LoadState.SUCCESS };
					List<Routine> someRoutines = parse(nestedObject, stateMachine);
					if(stateMachine[0].equals(LoadState.SUCCESS))
						routines.addAll(someRoutines);
					else currentState = LoadState.FAILURE;
				}
			else
			{
				currentState = LoadState.FAILURE;
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Parse error: did not recognize object " + object.toString() + " of type " + object.getClass().getName(), LoadState.FAILURE);
			}
		}
		else
		{
			currentState = LoadState.FAILURE;
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Parse error: null", currentState);
		}
		if(currentState.equals(LoadState.FAILURE) || routines.isEmpty())
		{
			resultingState[0] = LoadState.FAILURE;
			routines.clear();
		}
		return routines;
	}

	@Override
	protected String getObjectName(Routine routine){ return routine.getClass().getSimpleName();}

}
