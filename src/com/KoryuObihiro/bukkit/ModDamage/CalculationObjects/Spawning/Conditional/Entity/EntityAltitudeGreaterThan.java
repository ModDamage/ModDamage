package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class EntityAltitudeGreaterThan extends EntityConditionalSpawnCalculation 
{
	final int altitude;
	public EntityAltitudeGreaterThan(int altitude, boolean inverted, List<SpawnCalculation> calculations)
	{ 
		this.altitude = altitude;
		this.inverted = inverted;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().getBlockY() > altitude;}
}
