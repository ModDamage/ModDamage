package com.ModDamage.Backend.Aliasing;

import org.bukkit.block.Biome;

import com.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class BiomeAliaser extends CollectionAliaser<Biome> 
{
	public BiomeAliaser(){ super(AliasManager.Biome.name());}
	@Override
	protected Biome matchNonAlias(String key)
	{
		for(Biome biome : Biome.values())
			if(key.equalsIgnoreCase(biome.name()))
				return biome;
		return null;
	}
	@Override
	protected String getObjectName(Biome object){ return object.name();}
}
