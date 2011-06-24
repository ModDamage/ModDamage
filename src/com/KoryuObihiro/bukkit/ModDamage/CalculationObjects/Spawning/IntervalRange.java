package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class IntervalRange extends ChanceHealthCalculation 
{
	private int baseValue, intervalValue, rangeValue;
	public IntervalRange(int base, int interval, int interval_range)
	{ 
		baseValue = base;
		intervalValue = interval;
		rangeValue = interval_range;
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = baseValue + (intervalValue * (Math.abs(random.nextInt()%(rangeValue + 1))));}
}
