package com.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.RoutineObjects.Routine;
import com.ModDamage.RoutineObjects.Nested.NestedRoutine;

public class RoutineAliaser extends CollectionAliaser<Routine> 
{
	public RoutineAliaser(){ super(AliasManager.Routine.name());}
	
	@Override
	public boolean completeAlias(String key, Object values)
	{
		if(values instanceof List)
		{
			ModDamage.addToLogRecord(OutputPreset.INFO, "Adding " + name + " alias \"" + key + "\"");
			if(values.toString().contains(key))
			{
				ModDamage.changeIndentation(true);
				ModDamage.addToLogRecord(OutputPreset.WARNING, "Warning: \"" + key + "\" is self-referential!");
				ModDamage.changeIndentation(false);
			}
			
			List<Routine> matchedItems = new ArrayList<Routine>();
			if(!parseRoutines(matchedItems, values))
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error adding value " + values.toString());
				return false;
			}
			
			thisMap.get(key).addAll(matchedItems);
			return true;
		}
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error adding alias \"" + key + "\" - unrecognized value \"" + values.toString() + "\"");
		return false;
	}

	@Override
	public Collection<Routine> matchAlias(String key)
	{
		return thisMap.containsKey(key)?thisMap.get(key):null;
	}
	
	@Override
	@Deprecated
	protected Routine matchNonAlias(String key){ return null;}
	
	//Parse routine strings recursively
	public static boolean parseRoutines(List<Routine> target, Object object)
	{
		ModDamage.changeIndentation(true);
		boolean returnResult = recursivelyParseRoutines(target, object);
		ModDamage.changeIndentation(false);
		return returnResult;
	}
	@SuppressWarnings("unchecked")
	private static boolean recursivelyParseRoutines(List<Routine> target, Object object)
	{
		boolean encounteredError = false;
		if(object != null)
		{
			if(object instanceof String)
			{
				String string = (String) object;
				
				Routine routine = Routine.getNew(string);
				if(routine != null) target.add(routine);
				else
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid base routine " + " \"" + string + "\"");
					encounteredError = true;
				}
			}
			else if(object instanceof LinkedHashMap)
			{
				LinkedHashMap<String, Object> someHashMap = (LinkedHashMap<String, Object>)object;
				if(someHashMap.keySet().size() == 1)
					for(Entry<String, Object> entry : someHashMap.entrySet())//A properly-formatted nested routine is a LinkedHashMap with only one key.
					{
						NestedRoutine routine = NestedRoutine.getNew(entry.getKey(), entry.getValue());
						if(routine != null) target.add(routine);
						else
						{
							encounteredError = true;
							break;
						}
					}
				else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Parse error: invalid nested routine \"" + someHashMap.toString() + "\"");
			}
			else if(object instanceof List)
				for(Object nestedObject : (List<Object>)object)
				{
					if(!recursivelyParseRoutines(target, nestedObject))
						encounteredError = true;
				}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Parse error: did not recognize object " + object.toString() + " of type " + object.getClass().getName());
				encounteredError = true;
			}
		}
		else
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Parse error: null");
			encounteredError = true;
		}
		return !encounteredError;
	}

	@Override
	protected String getObjectName(Routine routine){ return routine.getClass().getSimpleName();}
}