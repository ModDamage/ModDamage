package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class WorldAliaser extends Aliaser<String> 
{
	private static final long serialVersionUID = 6446417315016119118L;

	public WorldAliaser() {super("World");}

	@Override
	protected String matchNonAlias(String key)
	{ 
		if(ModDamage.server.getWorld(key) != null) return ModDamage.server.getWorld(key).getName();
		return null;
	}

	@Override
	protected String getObjectName(String object){ return object;}
}
