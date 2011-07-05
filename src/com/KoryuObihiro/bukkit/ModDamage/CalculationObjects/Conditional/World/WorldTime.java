package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.World;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class WorldTime extends WorldConditionalCalculation 
{
	private boolean checkInverse;
	private long beginningTime;
	private long endTime;
	public WorldTime(boolean inverted, int beginningTime, int endTime, List<ModDamageCalculation> calculations)
	{
		this.inverted = inverted;
		this.calculations = calculations;
		this.beginningTime = beginningTime;
		this.endTime = endTime;
		checkInverse = beginningTime > endTime;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return checkTime(eventInfo.world);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return checkTime(eventInfo.world);}

	private boolean checkTime(World world)
	{
		return(checkInverse
				?(world.getTime() > beginningTime && world.getTime() < endTime)
				:(!(world.getTime() > beginningTime) || !(world.getTime() < endTime)));
	}
}
