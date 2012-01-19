package com.ModDamage.Backend.Aliasing;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class WorldAliaser extends CollectionAliaser<String> 
{
	public WorldAliaser() {super(AliasManager.World.name());}

	@Override
	protected String matchNonAlias(String key)
	{
		if(Bukkit.getWorld(key) == null)
		{
			ModDamage.addToLogRecord(OutputPreset.WARNING_STRONG, "Warning: world \"" + key + "\" does not currently exist.");
			return key;
		}
		return Bukkit.getWorld(key).getName();
	}

	@Override
	protected String getObjectName(String object){ return object;}

	@Override
	protected HashSet<String> getNewStorageClass(String value){ return new HashSet<String>(Arrays.asList(value));}

	@Override
	protected HashSet<String> getDefaultValue(){ return new HashSet<String>();}
}
