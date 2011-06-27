package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity;

import java.util.List;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class EntityBiome extends EntityConditionalSpawnCalculation 
{
	final Biome biome;
	public EntityBiome(Biome biome, boolean inverted, List<SpawnCalculation> calculations)
	{ 
		this.biome = biome;
		this.inverted = inverted;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().getBlock().getBiome().equals(biome);}
}
