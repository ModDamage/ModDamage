package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

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
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage)
	{
		if(checkInverse
				?(world.getTime() > beginningTime && world.getTime() < endTime)
				:(!(world.getTime() > beginningTime) || !(world.getTime() < endTime)))
			return makeCalculations(target, attacker, eventDamage);
		return eventDamage;
	}
}
