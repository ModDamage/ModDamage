package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.HashMap;
import java.util.List;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class BiomeSwitch extends EntitySwitchCalculation<Biome>
{
	public BiomeSwitch(boolean forAttacker, HashMap<Biome, List<ModDamageCalculation>> switchLabels)
	{
		super(forAttacker, switchLabels);
	}

	@Override
	protected Biome getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getBiome();}

	@Override
	protected Biome getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().getBlock().getBiome();}
}
