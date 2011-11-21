package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;

abstract public class Aliaser<Type, StoredInfoClass> extends HashMap<String, StoredInfoClass>
{
	private static final long serialVersionUID = -5035446508507898319L;
	final String name;
	protected LoadState loadState;
	
	Aliaser(String name){ this.name = name;}
	
	public StoredInfoClass matchAlias(String key)
	{
		if(this.containsKey(key))
			return this.get(key);
		Type value = matchNonAlias(key);
		if(value != null) return getNewStorageClass(value);
		ModDamage.addToLogRecord(OutputPreset.FAILURE, "No matching " + name + " alias or value \"" + key + "\"");
		return getDefaultValue();
	}

	abstract public boolean completeAlias(String key, Object nestedContent);

	abstract protected Type matchNonAlias(String key);
	
	abstract protected String getObjectName(Type object);
	
	public String getName(){ return name;}

	public LoadState getLoadState(){ return this.loadState;}
	
	@Override
	public void clear()
	{
		super.clear();
		loadState = LoadState.NOT_LOADED;
	}
	
	public LoadState load(LinkedHashMap<String, Object> rawAliases)
	{
		loadState = LoadState.NOT_LOADED;
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		if(rawAliases != null)
		{
			Set<String> foundAliases = rawAliases.keySet();
			if(!foundAliases.isEmpty())
			{
				loadState = LoadState.SUCCESS;
				ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, this.name + " aliases found, parsing...");
				for(String alias : foundAliases)
					this.put("_" + alias, getDefaultValue());
				for(String alias : foundAliases)
				{
					ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
					if(rawAliases.get(alias) != null)
					{
						if(!this.completeAlias("_" + alias, rawAliases.get(alias)))
							this.loadState = LoadState.FAILURE;
					}
					else ModDamage.addToLogRecord(OutputPreset.WARNING, "Found empty " + this.name.toLowerCase() + " alias \"" + alias + "\", ignoring...");
				}
				for(String alias : this.keySet())
					if(this.get(alias) == null)
						this.remove(alias);
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.WARNING, "Found " + this.name + " aliases node, but it was empty.");
			}
		}
		else
		{
			ModDamage.addToLogRecord(OutputPreset.WARNING, "No " + this.name + " aliases node found.");
		}
		return loadState;	
	}
	
	abstract protected StoredInfoClass getNewStorageClass(Type value);
	protected StoredInfoClass getDefaultValue(){ return null;}

	@SuppressWarnings("serial")
	abstract public static class SingleValueAliaser<Type> extends Aliaser<Type, Type>
	{
		SingleValueAliaser(String name){ super(name);}

		@Override
		public boolean completeAlias(String key, Object nestedContent)
		{
			if(nestedContent instanceof String)
			{
				Type matchedItem = matchAlias((String)nestedContent);
				if(matchedItem != null)
				{
					ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding value \"" + getObjectName(matchedItem) + "\"");
					this.put(key, matchedItem);
				}
			}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error adding alias \"" + key + "\" - unrecognized value \"" + nestedContent.toString() + "\"");
			return false;
		}

		@Override
		protected Type getNewStorageClass(Type value){ return null;}
	}
	
	@SuppressWarnings("serial")
	abstract public static class CollectionAliaser<InfoType> extends Aliaser<InfoType, Collection<InfoType>>
	{
		CollectionAliaser(String name){ super(name);}

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
					else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized object " + nestedContent.toString());
				}
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unrecognized object " + nestedContent.toString());
				return false;
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
							ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "Adding value \"" + getObjectName(value) + "\"");
							matchedItems.add(value);
						}
						else ModDamage.addToLogRecord(OutputPreset.WARNING, "Duplicate value \"" + getObjectName(value) + "\" - ignoring.");
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
			if(!failFlag) this.get(key).addAll(matchedItems);
			return !failFlag;
		}
		@Override @SuppressWarnings("unchecked")
		protected Collection<InfoType> getNewStorageClass(InfoType value){ return Arrays.asList(value);}
		@Override
		protected Collection<InfoType> getDefaultValue(){ return new ArrayList<InfoType>();}
	}
}