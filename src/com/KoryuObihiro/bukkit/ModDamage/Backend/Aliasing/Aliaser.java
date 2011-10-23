package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

abstract public class Aliaser<StorageClass extends Collection<Type>, Type> extends HashMap<String, StorageClass>
{
	private static final long serialVersionUID = -5035446508507898319L;
	HashMap<String, List<Type>> aliases;
	final String name;
	protected LoadState loadState;
	
	Aliaser(String name){ this.name = name;}

	public boolean completeAlias(String key, List<?> values)
	{
		//FIXME Do I work?
		key = "_" + key;
		List<Type> matchedItems = new ArrayList<Type>();
		ModDamage.addToLogRecord(DebugSetting.NORMAL, "Adding " + name + " alias \"" + key + "\"", LoadState.SUCCESS);
		ModDamage.indentation++;
		for(Object listedValue : values)
		{
			if(listedValue instanceof String)
			{
				StorageClass matchedList = matchAlias((String)listedValue);
				if(!matchedList.isEmpty())
				{
					for(Type value : matchedList)
					{
						if(!matchedItems.contains(value))
						{
							ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Adding value \"" + getObjectName(value) + "\"", LoadState.SUCCESS);
							matchedItems.add(value);
						}
						else ModDamage.addToLogRecord(DebugSetting.NORMAL, "Warning: duplicate value \"" + getObjectName(value) + "\" - ignoring.", LoadState.NOT_LOADED);
					}
				}
				else if(key.equalsIgnoreCase((String)listedValue))
					ModDamage.addToLogRecord(DebugSetting.NORMAL, "Warning: self-referential value \"" + key + "\" - ignoring.", LoadState.NOT_LOADED);
				else
				{
					ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: invalid value \"" + (String)listedValue + "\" - ignoring.", LoadState.FAILURE);
					ModDamage.indentation--;
					return false;//debug output already handled in failed matchAlias
				}
			}
			else
			{
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Unrecognized object " + listedValue.toString(), LoadState.FAILURE);
				ModDamage.indentation--;
				return false;
			}
		}
		ModDamage.indentation--;
		this.get(key).addAll(matchedItems);
		return true;
	}
	
	public StorageClass matchAlias(String key)
	{
		if(this.containsKey(key))
			return this.get(key);
		Type value = matchNonAlias(key);
		if(value != null) return getNewStorageClass(value);
		{
			ModDamage.indentation++;
			ModDamage.addToLogRecord(DebugSetting.QUIET, "No matching " + name + " alias or value \"" + key + "\"", LoadState.FAILURE);
			ModDamage.indentation--;
		}
		
		return getNewStorageClass();
	}

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
	
	public LoadState load(ConfigurationNode aliasesNode)
	{
		loadState = LoadState.NOT_LOADED;
		ConfigurationNode specificAliasesNode = null;
		for(String key : aliasesNode.getKeys())
			if(key.equalsIgnoreCase(this.name))
			{
				specificAliasesNode = aliasesNode.getNode(key);
				break;
			}
		if(specificAliasesNode != null)
		{
			if(!specificAliasesNode.getKeys().isEmpty())
			{
				this.loadState = LoadState.SUCCESS;
				ModDamage.addToLogRecord(DebugSetting.VERBOSE, this.name + " aliases found, parsing...", LoadState.SUCCESS);
			
				HashMap<String, List<?>> rawAliases = new HashMap<String, List<?>>();
				for(String alias : specificAliasesNode.getKeys())
				{
					rawAliases.put(alias, (List<?>)specificAliasesNode.getList(alias));
					this.put("_" + alias, getNewStorageClass());
				}
				for(String alias : rawAliases.keySet())
				{
					if(rawAliases.get(alias).isEmpty())
						ModDamage.addToLogRecord(DebugSetting.VERBOSE, "Found empty " + this.name.toLowerCase() + " alias \"" + alias + "\", ignoring...", LoadState.NOT_LOADED);
					else if(!this.completeAlias(alias, rawAliases.get(alias)))
						this.loadState = LoadState.FAILURE;
				}
				for(String alias : this.keySet())
					if(this.get(alias).isEmpty())
						this.remove(alias);
			}
		}
		else ModDamage.addToLogRecord(DebugSetting.VERBOSE, "No " + this.name + " aliases node found.", LoadState.NOT_LOADED);
		return loadState;
	}
	
	abstract protected StorageClass getNewStorageClass(Type value);
	abstract protected StorageClass getNewStorageClass();
}
