package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health;

public class IntervalRangeCalculation extends ChanceCalculation 
{
	private int baseValue, intervalValue, rangeValue;
	public IntervalRangeCalculation(int base, int interval, int interval_range)
	{ 
		baseValue = base;
		intervalValue = interval;
		rangeValue = interval_range;
	}
	@Override
	public int calculate(){ return baseValue + (intervalValue * (Math.abs(random.nextInt()%(rangeValue + 1))));}
}
