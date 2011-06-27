package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import org.bukkit.World.Environment;
import org.bukkit.block.Biome;

public class CalculationUtility
{
	//Frankly, most of the stuff in here should be considered for implementation into Bukkit. :<
	public static Biome matchBiome(String biomeName)
	{
		if(biomeName.equalsIgnoreCase("DESERT")) return Biome.DESERT;
		else if(biomeName.equalsIgnoreCase("FOREST")) return Biome.FOREST;
		else if(biomeName.equalsIgnoreCase("HELL")) return Biome.HELL;
		else if(biomeName.equalsIgnoreCase("ICE_DESERT")) return Biome.ICE_DESERT;
		else if(biomeName.equalsIgnoreCase("PLAINS")) return Biome.PLAINS;
		else if(biomeName.equalsIgnoreCase("RAINFOREST")) return Biome.RAINFOREST;
		else if(biomeName.equalsIgnoreCase("SAVANNA")) return Biome.SAVANNA;
		else if(biomeName.equalsIgnoreCase("SEASONAL_FOREST")) return Biome.SEASONAL_FOREST;
		else if(biomeName.equalsIgnoreCase("SHRUBLAND")) return Biome.SHRUBLAND;
		else if(biomeName.equalsIgnoreCase("SKY")) return Biome.SKY;
		else if(biomeName.equalsIgnoreCase("SWAMPLAND")) return Biome.SWAMPLAND;
		else if(biomeName.equalsIgnoreCase("TAIGA")) return Biome.TAIGA;
		else if(biomeName.equalsIgnoreCase("TUNDRA")) return Biome.TUNDRA;
		return null;
	}
	
	public static Environment matchEnvironment(String environmentName)
	{
		if(environmentName.equalsIgnoreCase("NORMAL")) return Environment.NORMAL;
		else if(environmentName.equalsIgnoreCase("NETHER")) return Environment.NETHER;
		else if(environmentName.equalsIgnoreCase("SKYLANDS")) return Environment.SKYLANDS;
		return null;
	}

}
