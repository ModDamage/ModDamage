package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health;

public class LiteralRangeCalculation extends ChanceCalculation 
{
	private int lowerBound, upperBound;
	public LiteralRangeCalculation(int lower, int upper)
	{ 
		lowerBound = lower;
		upperBound = upper;
	}
	@Override
	public int calculate()
	{ return lowerBound + Math.abs(random.nextInt()%(upperBound - lowerBound + 1));}
}
