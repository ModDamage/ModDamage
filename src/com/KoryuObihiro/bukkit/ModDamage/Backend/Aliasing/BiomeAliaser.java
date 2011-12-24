package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.CollectionAliaser;

public class BiomeAliaser extends CollectionAliaser<Biome> 
{
	public BiomeAliaser(){ super(AliasManager.Biome.name());}
	@Override
	protected Biome matchNonAlias(String key){ return Biome.valueOf(key.toUpperCase());}
	@Override
	protected String getObjectName(Biome object){ return object.name();}
}
