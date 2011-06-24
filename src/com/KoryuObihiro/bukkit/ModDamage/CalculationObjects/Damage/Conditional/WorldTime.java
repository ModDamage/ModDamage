package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class WorldTime extends WorldConditionalCalculation 
{
	private boolean checkInverse;
	private long beginningTime;
	private long endTime;
	public WorldTime(int beginningTime, int endTime, List<DamageCalculation> calculations)
	{
		this.calculations = calculations;
		this.beginningTime = beginningTime;
		this.endTime = endTime;
		checkInverse = beginningTime > endTime;
	}
	@Override
	public void calculate(EventInfo eventInfo)
	{
		if(checkInverse
				?(eventInfo.world.getTime() > beginningTime && eventInfo.world.getTime() < endTime)
				:(!(eventInfo.world.getTime() > beginningTime) || !(eventInfo.world.getTime() < endTime)))
			 makeCalculations(eventInfo);
	}
}
