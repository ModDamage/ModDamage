package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class BiomeAliaser extends Aliaser<Biome> 
{
	private static final long serialVersionUID = 7448767002533858908L;
	
	public BiomeAliaser(){ super("Biome");}
	@Override
	protected Biome matchNonAlias(String key){ return ModDamage.matchBiome(key);}
	@Override
	protected String getObjectName(Biome object){ return object.name();}
}
