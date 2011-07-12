package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

public class LiteralRange extends ChanceCalculation 
{
	private int lowerBound, upperBound;
	public LiteralRange(int lower, int upper)
	{ 
		lowerBound = lower;
		upperBound = upper;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = lowerBound + Math.abs(random.nextInt()%(upperBound - lowerBound + 1));}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = lowerBound + Math.abs(random.nextInt()%(upperBound - lowerBound + 1));}
	
	public static void register()
	{
		CalculationUtility.register(LiteralRange.class, Pattern.compile("range\\.([0-9]*)\\.([0-9]*)", Pattern.CASE_INSENSITIVE));
	}
}
