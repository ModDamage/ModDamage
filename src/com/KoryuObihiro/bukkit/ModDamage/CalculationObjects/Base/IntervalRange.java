package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

public class IntervalRange extends ChanceCalculation 
{
	private int baseValue, intervalValue, rangeValue;
	public IntervalRange(int base, int interval, int interval_range)
	{ 
		baseValue = base;
		intervalValue = interval;
		rangeValue = interval_range;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = baseValue + (intervalValue * (Math.abs(random.nextInt()%(rangeValue + 1))));}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = baseValue + (intervalValue * (Math.abs(random.nextInt()%(rangeValue + 1))));}
	
	public static IntervalRange getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new IntervalRange(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
		return null;
	}
	
	public static void register()
	{
		CalculationUtility.register(DiceRoll.class, Pattern.compile("range_int\\.([0-9]+)\\.([0-9]+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
