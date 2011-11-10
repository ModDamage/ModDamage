package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class WorldAliaser extends CollectionAliaser<String> 
{
	private static final long serialVersionUID = 6446417315016119118L;

	public WorldAliaser() {super("World");}

	@Override
	protected String matchNonAlias(String key)
	{ 
		if(Bukkit.getWorld(key) != null) return Bukkit.getWorld(key).getName();
		return null;
	}

	@Override
	protected String getObjectName(String object){ return object;}

	@Override
	protected HashSet<String> getNewStorageClass(String value){ return new HashSet<String>(Arrays.asList(value));}

	@Override
	protected HashSet<String> getDefaultValue(){ return new HashSet<String>();}
}
