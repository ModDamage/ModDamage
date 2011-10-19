package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.Bukkit;

public class WorldAliaser extends Aliaser<String> 
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
}
