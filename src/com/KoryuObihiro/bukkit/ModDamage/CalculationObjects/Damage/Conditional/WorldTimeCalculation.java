package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;

public class WorldTimeCalculation extends WorldConditionalCalculation 
{
	private boolean checkInverse;
	private long beginningTime;
	private long endTime;
	public WorldTimeCalculation(World world, long beginningTime, long endTime)
	{
		this.world = world;
		this.beginningTime = beginningTime;
		this.endTime = endTime;
		checkInverse = beginningTime > endTime;
	}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage)
	{
		if(checkInverse
				?(world.getTime() > beginningTime && world.getTime() < endTime)
				:(!(world.getTime() > beginningTime) || !(world.getTime() < endTime)))
			return makeCalculations(eventInfo, eventDamage);
		return eventDamage;
	}
}
