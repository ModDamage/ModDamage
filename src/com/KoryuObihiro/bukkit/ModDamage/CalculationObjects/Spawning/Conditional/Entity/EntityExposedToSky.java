package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity;

import java.util.List;

import org.bukkit.Material;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class EntityExposedToSky extends EntityConditionalSpawnCalculation 
{
	public EntityExposedToSky(boolean inverted, List<SpawnCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo)
	{ 
		int i = eventInfo.entity.getLocation().getBlockX();
		int k = eventInfo.entity.getLocation().getBlockZ();
		for(int j = eventInfo.entity.getLocation().getBlockY(); j < 128; j++)
			//FIXME Add more block types!...might be expensive though.
			if(!eventInfo.world.getBlockAt(i, j, k).equals(Material.AIR))
				return false;
		return true;
	}
}
