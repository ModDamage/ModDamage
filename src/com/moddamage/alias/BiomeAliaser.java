package com.moddamage.alias;

import java.util.Collection;

import org.bukkit.block.Biome;

import com.moddamage.alias.Aliaser.CollectionAliaser;

public class BiomeAliaser extends CollectionAliaser<Biome> 
{
	public static BiomeAliaser aliaser = new BiomeAliaser();
	public static Collection<Biome> match(String string) { return aliaser.matchAlias(string); }
	
	public BiomeAliaser(){ super(AliasManager.Biome.name()); }
	@Override
	protected Biome matchNonAlias(String key)
	{
		for(Biome biome : Biome.values())
			if(key.equalsIgnoreCase(biome.name()))
				return biome;
		return null;
	}
	//@Override
	//protected String getObjectName(Biome object){ return object.name(); }
}
