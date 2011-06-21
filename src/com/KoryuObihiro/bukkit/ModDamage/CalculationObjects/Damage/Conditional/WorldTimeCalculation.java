package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import org.bukkit.World;

public class WorldTimeCalculation extends WorldConditionalCalculation 
{
	private long beginningTime;
	private long endTime;
	public WorldTimeCalculation(World world, long beginningTime, long endTime)
	{
		this.world = world;
		this.beginningTime = beginningTime;
		this.endTime = endTime;
	}
	@Override
	public int calculate(int eventDamage)
	{
		if(world.getTime() > beginningTime && world.getTime() < endTime)
			return calculate(eventDamage);
		return eventDamage;
	}
}
