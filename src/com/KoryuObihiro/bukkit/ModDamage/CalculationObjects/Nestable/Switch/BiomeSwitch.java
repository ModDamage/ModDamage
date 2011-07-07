package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class BiomeSwitch extends SwitchCalculation
{
	final boolean forAttacker;
	protected final HashMap<Biome, List<ModDamageCalculation>> switchLabels;
	public BiomeSwitch(boolean forAttacker, HashMap<Biome, List<ModDamageCalculation>> switchLabels)
	{
		this.forAttacker = forAttacker;
		this.switchLabels = switchLabels;
	}
	
	@Override
	public void calculate(DamageEventInfo eventInfo) 
	{
		Biome biome = (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getBiome();
		if(biome != null && switchLabels.containsKey(biome))
			for(ModDamageCalculation calculation : switchLabels.get(biome))
				calculation.calculate(eventInfo);
	}

	@Override
	public void calculate(SpawnEventInfo eventInfo) 
	{
		Biome biome = eventInfo.entity.getLocation().getBlock().getBiome();
		if(biome != null && switchLabels.containsKey(biome))
			for(ModDamageCalculation calculation : switchLabels.get(biome))
				calculation.calculate(eventInfo);
	}

}
