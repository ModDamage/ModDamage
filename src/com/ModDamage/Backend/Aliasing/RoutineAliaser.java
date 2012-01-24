package com.ModDamage.Backend.Aliasing;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class RoutineAliaser extends Aliaser<Routine, Routines> 
{
	public static RoutineAliaser aliaser = new RoutineAliaser();
	public static Routines match(String string) { return aliaser.matchAlias(string); }
	
	public RoutineAliaser(){ super("Routine");}
	
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
			
			Routines routines = parseRoutines(values);
			if(routines == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error adding value " + values.toString());
				return false;
			}
			
			thisMap.put(key, routines);
			return true;
		}
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error adding alias \"" + key + "\" - unrecognized value \"" + values.toString() + "\"");
		return false;
	}

	@Override
	public Routines matchAlias(String key)
	{
		return thisMap.get(key);
	}
	
	@Override
	@Deprecated
	protected Routine matchNonAlias(String key){ return null;}
	
	//Parse routine strings recursively
	public static Routines parseRoutines(Object object)
	{
		Routines routines = new Routines();
		ModDamage.changeIndentation(true);
		boolean returnResult = recursivelyParseRoutines(routines.routines, object);
		ModDamage.changeIndentation(false);
		if (!returnResult) return null;
		return routines;
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

	@Override
	protected Routines getNewStorageClass(Routine value)
	{
		return new Routines();
	}
	
	@Override
	protected Routines getDefaultValue()
	{
		return new Routines();
	}
}