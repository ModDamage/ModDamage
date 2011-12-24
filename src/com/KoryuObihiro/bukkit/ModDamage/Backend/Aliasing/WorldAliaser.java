package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

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
