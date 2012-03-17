package com.ModDamage.Alias;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.LoadState;
import com.ModDamage.PluginConfiguration.OutputPreset;

abstract public class Aliaser<Type, StoredInfoClass>
{
	private Map<String, StoredInfoClass> thisMap = new HashMap<String, StoredInfoClass>();
	protected final String name;
	protected LoadState loadState = LoadState.NOT_LOADED;
	
	Aliaser(String name){ this.name = name; }
	
	/*public StoredInfoClass matchAlias(String key)
	{
		if(thisMap.containsKey(key))
			return thisMap.get(key);
		return null;
		/*
		Type value = matchNonAlias(key);
		if(value != null) return getNewStorageClass(value);
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "No matching " + name + " alias or value \"" + key + "\"");
		return getDefaultValue();* /
	}*/

	abstract public boolean completeAlias(String key, Object nestedContent);

	//abstract protected Type matchNonAlias(String key);
	
	//abstract protected String getObjectName(Type object);
	
	public String getName(){ return name; }

	public LoadState getLoadState(){ return this.loadState; }

	public boolean hasAlias(String key)
	{
		return thisMap.containsKey(key);
	}
	
	public StoredInfoClass getAlias(String key)
	{
		return thisMap.get(key);
	}
	
	public void putAlias(String key, StoredInfoClass obj)
	{
		thisMap.put(key, obj);
	}
	
	public void clear()
	{
		thisMap.clear();
		loadState = LoadState.NOT_LOADED;
	}
	
	public void load(LinkedHashMap<String, Object> rawAliases)
	{
		clear();
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		if(rawAliases != null)
		{
			if(!rawAliases.isEmpty())
			{
				loadState = LoadState.SUCCESS;
				ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, this.name + " aliases found, parsing...");
				for(String alias : rawAliases.keySet())
					thisMap.put("_" + alias, getDefaultValue());
				for(Entry<String, Object> entry : rawAliases.entrySet())
				{
					ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
					if(entry.getValue() != null)
					{
						if(!this.completeAlias("_" + entry.getKey(), entry.getValue()))
							this.loadState = LoadState.FAILURE;
					}
					else ModDamage.addToLogRecord(OutputPreset.WARNING, "Found empty " + this.name.toLowerCase() + " alias \"" + entry.getKey() + "\", ignoring...");
				}
				for(String alias : thisMap.keySet())
					if(thisMap.get(alias) == null)
						thisMap.remove(alias);
			}
		}
	}
	
	//abstract protected StoredInfoClass getNewStorageClass(Type value);
	protected StoredInfoClass getDefaultValue(){ return null; }

	abstract public static class SingleValueAliaser<Type> extends Aliaser<Type, Type>
	{
		SingleValueAliaser(String name){ super(name); }

		@Override
		public boolean completeAlias(String key, Object nestedContent)
		{
			if(nestedContent instanceof String)
			{
				Type matchedItem = matchAlias((String)nestedContent);
				if(matchedItem != null)
				{
					//ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding value \"" + getObjectName(matchedItem) + "\"");
					putAlias(key, matchedItem);
					return true;
				}
			}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error adding alias \"" + key + "\" - unrecognized value \"" + nestedContent.toString() + "\"");
			return false;
		}
		
		public Type matchAlias(String key)
		{
			if(hasAlias(key))
				return getAlias(key);
			
			
			Type value = matchNonAlias(key);
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "No matching " + name + " alias or value \"" + key + "\"");
			return value;
		}
		
		abstract Type matchNonAlias(String string);

		//@Override
		//protected Type getNewStorageClass(Type value){ return null; }
	}
	
	abstract public static class CollectionAliaser<InfoType> extends Aliaser<InfoType, Collection<InfoType>>
	{
		CollectionAliaser(String name){ super(name); }
		
		public void putAllAliases(String key, Collection<InfoType> items)
		{
			Collection<InfoType> aliases = getAlias(key);
			if (aliases == null)
			{
				putAlias(key, items);
				return;
			}
			
			aliases.addAll(items);
		}

		@Override
		public boolean completeAlias(String key, Object nestedContent)
		{
			boolean failFlag = false;
			HashSet<InfoType> matchedItems = new HashSet<InfoType>();
			List<String> foundValues = new ArrayList<String>();
			ModDamage.addToLogRecord(OutputPreset.INFO, "Adding " + name + " alias \"" + key + "\"");
			ModDamage.changeIndentation(true);
			if(nestedContent instanceof String)
			{
				foundValues.add(((String)nestedContent));
			}
			else if(nestedContent instanceof List)
			{
				for(Object object : ((List<?>)nestedContent))
				{
					if(object instanceof String)
						foundValues.add(((String)object));
					else
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized object " + nestedContent.toString());
						failFlag = true;
					}
				}
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized object " + nestedContent.toString());
				failFlag = true;
			}
			
			for(Object listedValue : foundValues)
			{
				Collection<InfoType> matchedList = matchAlias((String)listedValue);
				if(!matchedList.isEmpty())
				{
					for(InfoType value : matchedList)
					{
						if(!matchedItems.contains(value))
						{
							//ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding value \"" + getObjectName(value) + "\"");
							matchedItems.add(value);
						}
						//else
						//	ModDamage.addToLogRecord(OutputPreset.WARNING, "Duplicate value \"" + getObjectName(value) + "\" - ignoring.");
					}
				}
				else if(key.equalsIgnoreCase((String)listedValue))
					ModDamage.addToLogRecord(OutputPreset.WARNING, "Self-referential value \"" + key + "\" - ignoring.");
				else
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: invalid value \"" + (String)listedValue + "\" - ignoring.");
					failFlag = true;//debug output already handled in failed matchAlias
				}
			}
			ModDamage.changeIndentation(false);
			if(!failFlag) putAllAliases(key, matchedItems);
			return !failFlag;
		}
		
		//@Override
		public Collection<InfoType> matchAlias(String key)
		{
			if(hasAlias(key))
				return getAlias(key);
			
			
			boolean failFlag = false;
			List<InfoType> values = new ArrayList<InfoType>();
			if (key != null)
			{
				for(String valueString : key.split(","))
				{
					InfoType value = matchNonAlias(valueString);
					if(value != null) values.add(value);
					else failFlag = true;
				}
			}
			if(!failFlag && !values.isEmpty()) return values;
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "No matching " + name + " alias or value \"" + key + "\"");
			return getDefaultValue();
		}

		abstract protected InfoType matchNonAlias(String valueString);

		@Override
		protected Collection<InfoType> getDefaultValue(){ return new ArrayList<InfoType>(); }
		
		//@Override @SuppressWarnings("unchecked")
		//protected Collection<InfoType> getNewStorageClass(InfoType value){ return Arrays.asList(value); }
	}
}