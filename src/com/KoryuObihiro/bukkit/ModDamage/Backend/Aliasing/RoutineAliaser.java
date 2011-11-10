package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class RoutineAliaser extends CollectionAliaser<Routine> 
{
	private static final long serialVersionUID = -2744471820826321788L;
	public RoutineAliaser(){ super("Routine");}
	
	public boolean completeAlias(String key, List<?> values)
	{
		key = "_" + key;
		ModDamage.addToLogRecord(DebugSetting.NORMAL, "Adding " + name + " alias \"" + key + "\"", LoadState.SUCCESS);
		
		ModDamage.indentation++;
		if(values.toString().contains(key))
			ModDamage.addToLogRecord(DebugSetting.NORMAL, "Warning: \"" + key + "\" is self-referential!", LoadState.NOT_LOADED);
		LoadState[] addStateMachine = {LoadState.SUCCESS};
		
		ModDamage.indentation++;
		List<Routine> matchedItems = parse(values, addStateMachine);
		ModDamage.indentation--;
		
		if(!addStateMachine[0].equals(LoadState.SUCCESS))
		{
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Error adding value " + values.toString(), loadState);
			ModDamage.indentation--;
			return false;
		}
		ModDamage.indentation--;
		
		this.get(key).addAll(matchedItems);
		return true;
	}

	@Override
	public Collection<Routine> matchAlias(String key)
	{
		return this.containsKey(key)?this.get(key):null;
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
					Collection<Routine> aliasedRoutines = AliasManager.matchRoutineAlias((String)object);
					if(aliasedRoutines != null)
					{
						ModDamage.addToLogRecord(DebugSetting.NORMAL, "Alias: \"" + ((String)object).substring(1) + "\"", LoadState.SUCCESS);
						routines.addAll(aliasedRoutines);
					}
					else ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid routine alias " + ((String)object).substring(1), LoadState.FAILURE);
				}
				if(routines.isEmpty())
				{
					Routine routine = Routine.getNew((String)object);
					if(routine != null) routines.add(routine);
					else
					{
						currentState = LoadState.FAILURE;
						ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid base routine " + " \"" + (String)object + "\"", currentState);
					}
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
						if(routine != null) routines.add(routine);
						else currentState = LoadState.FAILURE;
					}
				else
				{
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Parse error: bad nested routine \"" + someHashMap.toString() + "\"", LoadState.FAILURE);
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