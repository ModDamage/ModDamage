package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.World;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class WorldTime extends WorldConditionalSpawnCalculation 
{
	private boolean checkInverse;
	private long beginningTime;
	private long endTime;
	public WorldTime(boolean inverted, int beginningTime, int endTime, List<SpawnCalculation> calculations)
	{
		this.inverted = inverted;
		this.calculations = calculations;
		this.beginningTime = beginningTime;
		this.endTime = endTime;
		checkInverse = beginningTime > endTime;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo)
	{
		return(checkInverse
				?(eventInfo.world.getTime() > beginningTime && eventInfo.world.getTime() < endTime)
				:(!(eventInfo.world.getTime() > beginningTime) || !(eventInfo.world.getTime() < endTime)));
	}
}
