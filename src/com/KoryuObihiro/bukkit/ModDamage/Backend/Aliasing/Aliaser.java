package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.util.config.ConfigurationNode;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

abstract public class Aliaser<Type> extends HashMap<String, List<Type>>
{
	private static final long serialVersionUID = -5035446508507898319L;
	HashMap<String, List<Type>> aliases;
	final String name;
	protected LoadState loadState;
	
	Aliaser(String name){ this.name = name;}

	public boolean addAlias(String key, List<Object> values)
	{
		if(this.containsKey(key)) return false;
		List<Type> matchedItems = new ArrayList<Type>();
		ModDamage.addToLogRecord(DebugSetting.NORMAL, 0, "Adding " + name + " alias \"" + key + "\"", LoadState.SUCCESS);
		for(Object listedValue : values)
		{
			if(listedValue instanceof String)
			{
				List<Type> matchedList = matchAlias((String)listedValue);
				if(!matchedList.isEmpty())
				{
					for(Type value : matchedList)
					{
						if(!matchedItems.contains(value))
						{
							ModDamage.addToLogRecord(DebugSetting.VERBOSE, 1, "Adding value \"" + getObjectName(value) + "\"", LoadState.SUCCESS);
							matchedItems.add(value);
						}
						else ModDamage.addToLogRecord(DebugSetting.NORMAL, 1, "Error: duplicate value \"" + getObjectName(value) + "\" - ignoring.", LoadState.NOT_LOADED);
					}
				}
				else return false;//debug output already handled in failed matchAlias
			}
			else
			{
				ModDamage.addToLogRecord(DebugSetting.QUIET, 1, "Unrecognized object " + listedValue.toString(), LoadState.FAILURE);
				return false;
			}
		}
		this.put("_" + key, matchedItems);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<Type> matchAlias(String key)
	{
		if(this.containsKey(key))
			return this.get(key);
		Type value = matchNonAlias(key);
		if(value != null) return Arrays.asList(value);
		ModDamage.addToLogRecord(DebugSetting.QUIET, 1, "No matching " + name + " alias or value \"" + key + "\"", LoadState.FAILURE);
		return new ArrayList<Type>();
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
				ModDamage.addToLogRecord(DebugSetting.VERBOSE, 0, this.name + " aliases found, parsing...", LoadState.SUCCESS);
				for(String alias : specificAliasesNode.getKeys())
				{
					List<Object> values = specificAliasesNode.getList(alias);
					if(values.isEmpty())
						ModDamage.addToLogRecord(DebugSetting.VERBOSE, 0, "Found empty " + this.name.toLowerCase() + " alias \"" + alias + "\", ignoring...", LoadState.NOT_LOADED);
					else if(!this.addAlias(alias, values))
						this.loadState = LoadState.FAILURE;
				}
			}
		}
		else ModDamage.addToLogRecord(DebugSetting.VERBOSE, 0, "No " + this.name + " aliases node found.", LoadState.NOT_LOADED);
		return loadState;
	}
}
