package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class EntityOnBlock extends EntityConditionalSpawnCalculation 
{
	final Material material;
	public EntityOnBlock(Material material, boolean inverted, List<SpawnCalculation> calculations)
	{ 
		this.material = material;
		this.inverted = inverted;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().add(0, -1, 0).getBlock().getType().equals(material);}
}
