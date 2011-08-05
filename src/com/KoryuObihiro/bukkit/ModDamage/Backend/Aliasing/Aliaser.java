package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

abstract public class Aliaser<Type> extends HashMap<String, List<Type>>
{
	private static final long serialVersionUID = -5035446508507898319L;
	HashMap<String, List<Type>> aliases;
	final String name;
	
	Aliaser(String name)
	{
		super();
		this.name = name;
	}

	public boolean addAlias(String key, List<String> values)
	{
		if(this.containsKey(key)) return false;
		List<Type> matchedItems = new ArrayList<Type>();
		for(String listedValue : values)
		{
			List<Type> matchedList = matchAlias(listedValue);
			if(!matchedList.isEmpty())
				for(Type value : matchedList)
				{
					if(!matchedItems.contains(value))
						matchedItems.add(value);
					else
					{
						ModDamage.addToConfig(DebugSetting.NORMAL, 1, "Error: duplicate value " + getName(value), LoadState.NOT_LOADED);
						return false;
					}
				}
			else return false;
		}
		this.put("_" + key, matchedItems);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<Type> matchAlias(String key)
	{
		if(this.containsKey(key.toLowerCase())) 
			return this.get(key);
		Type value = matchNonAlias(key);
		if(value != null) return Arrays.asList(value);
		ModDamage.addToConfig(DebugSetting.QUIET, 1, "No matching " + name + " alias or value \"" + key + "\"", LoadState.FAILURE);
		return new ArrayList<Type>();
	}
	
	abstract protected Type matchNonAlias(String key);
	
	abstract protected String getName(Type object);
}
