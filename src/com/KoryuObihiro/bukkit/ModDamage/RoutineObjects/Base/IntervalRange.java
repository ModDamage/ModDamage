package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class IntervalRange extends Chanceroutine 
{
	private int baseValue, intervalValue, rangeValue;
	public IntervalRange(String configString, int base, int interval, int interval_range)
	{ 
		super(configString);
		baseValue = base;
		intervalValue = interval;
		rangeValue = interval_range;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = baseValue + (intervalValue * (Math.abs(random.nextInt()%(rangeValue + 1))));}
	
	public static IntervalRange getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new IntervalRange(matcher.group(), Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
		return null;
	}
	
	public static void register()
	{
		Routine.registerBase(DiceRoll.class, Pattern.compile("range_int(?:\\." + Routine.dynamicIntegerPart + "){2}", Pattern.CASE_INSENSITIVE));
	}
}
