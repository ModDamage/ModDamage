package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class LiteralRange extends ChanceHealthCalculation 
{
	private int lowerBound, upperBound;
	public LiteralRange(int lower, int upper)
	{ 
		lowerBound = lower;
		upperBound = upper;
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = lowerBound + Math.abs(random.nextInt()%(upperBound - lowerBound + 1));}
}
