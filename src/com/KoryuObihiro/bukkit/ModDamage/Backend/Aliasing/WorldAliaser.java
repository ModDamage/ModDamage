package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class WorldAliaser extends Aliaser<String> 
{
	private static final long serialVersionUID = 6446417315016119118L;

	public WorldAliaser() {super("World");}

	@Override
	protected String matchNonAlias(String key)
	{ 
		for(World world : ModDamage.server.getWorlds())
			if(world.getName().equals(key))
				return key;
		return null;
	}

	@Override
	protected String getObjectName(String object){ return object;}
}
