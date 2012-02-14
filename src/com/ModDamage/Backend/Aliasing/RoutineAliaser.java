package com.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routine;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class RoutineAliaser extends Aliaser<Object, Object>
{
	public static RoutineAliaser aliaser = new RoutineAliaser();
	public static Routines match(String string, EventInfo info) { return aliaser.matchAlias(string, info); }
	
	public RoutineAliaser() { super("Routine"); }
	
	private static class AliasInfoPair {
		private final String alias;
		private final EventInfo info;
		
		public AliasInfoPair(String alias, EventInfo info)
		{
			this.alias = alias;
			this.info = info;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + alias.hashCode();
			result = prime * result + info.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			AliasInfoPair other = (AliasInfoPair) obj;
			if (!alias.equals(other.alias)) return false;
			if (!info.equals(other.info)) return false;
			return true;
		}
	}
	
	public final Map<AliasInfoPair, Routines> aliasedRoutines = new HashMap<AliasInfoPair, Routines>();
	
	
	public boolean completeAlias(String key, Object values)
	{
		if(values instanceof List)
		{
			ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding Routine alias \"" + key + "\"");
			/*if(values.toString().contains(key))
			{
				ModDamage.changeIndentation(true);
				ModDamage.addToLogRecord(OutputPreset.WARNING, "Warning: \"" + key + "\" is self-referential!");
				ModDamage.changeIndentation(false);
			}*/
			
			thisMap.put(key, values);
			return true;
		}
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error adding alias \"" + key + "\" - unrecognized value \"" + values.toString() + "\"");
		return false;
	}
	
	
	private static boolean isParsingAlias = false;
	public static boolean isParsingAlias() { return isParsingAlias; }
	private static List<Runnable> runWhenDone = new ArrayList<Runnable>();
	
	public static void whenDoneParsingAlias(Runnable runnable) {
		if (isParsingAlias) runWhenDone.add(runnable);
		else runnable.run();
	}
	

	public Routines matchAlias(String alias, EventInfo info)
	{
		AliasInfoPair infoPair = new AliasInfoPair(alias, info);
		if (aliasedRoutines.containsKey(infoPair)) return aliasedRoutines.get(infoPair);
		
		
		Object values = thisMap.get(alias);
		if (values == null)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown alias: \"" + alias + "\"");
			return null;
		}
		ModDamage.addToLogRecord(OutputPreset.INFO, "Routines in " + alias);
		isParsingAlias = true;
		Routines routines = parseRoutines(values, info);
		isParsingAlias = false;
		aliasedRoutines.put(infoPair, routines);
		if(routines == null)
		{
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error parsing " + values.toString());
			runWhenDone.clear();
			return null;
		}
		if (!runWhenDone.isEmpty())
		{
			List<Runnable> toRun = runWhenDone;
			runWhenDone = new ArrayList<Runnable>();
			for (Runnable runnable : toRun)
				runnable.run();
		}
		return routines;
	}
	
	//Parse routine strings recursively
	public static Routines parseRoutines(Object object, EventInfo info)
	{
		Routines routines = new Routines();
		ModDamage.changeIndentation(true);
		boolean returnResult = recursivelyParseRoutines(routines.routines, object, info);
		ModDamage.changeIndentation(false);
		if (!returnResult) return null;
		return routines;
	}
	@SuppressWarnings("unchecked")
	private static boolean recursivelyParseRoutines(List<Routine> target, Object object, EventInfo info)
	{
		boolean encounteredError = false;
		if(object != null)
		{
			if(object instanceof String)
			{
				String string = (String) object;
				
				Routine routine = Routine.getNew(string, info);
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
						NestedRoutine routine = NestedRoutine.getNew(entry.getKey(), entry.getValue(), info);
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
					if(!recursivelyParseRoutines(target, nestedObject, info))
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

	/*@Override
	protected String getObjectName(Routine routine){ return routine.getClass().getSimpleName(); }

	@Override
	protected Routines getNewStorageClass(Routine value)
	{
		return new Routines();
	}
	
	@Override
	protected Routines getDefaultValue()
	{
		return new Routines();
	}*/
}