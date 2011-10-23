package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class BiomeAliaser extends Aliaser<HashSet<Biome>, Biome> 
{
	private static final long serialVersionUID = 7448767002533858908L;
	
	public BiomeAliaser(){ super("Biome");}
	@Override
	protected Biome matchNonAlias(String key){ return ModDamage.matchBiome(key);}
	@Override
	protected String getObjectName(Biome object){ return object.name();}

	@Override
	protected HashSet<Biome> getNewStorageClass(Biome value){ return new HashSet<Biome>(Arrays.asList(value));}

	@Override
	protected HashSet<Biome> getNewStorageClass(){ return new HashSet<Biome>();}
}
