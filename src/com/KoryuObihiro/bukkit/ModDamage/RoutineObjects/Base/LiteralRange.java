package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class LiteralRange extends Chanceroutine 
{
	private int lowerBound, upperBound;
	public LiteralRange(String configString, int lower, int upper)
	{ 
		super(configString);
		lowerBound = lower;
		upperBound = upper;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = lowerBound + Math.abs(random.nextInt()%(upperBound - lowerBound + 1));}
	
	public static LiteralRange getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new LiteralRange(matcher.group(), Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
		return null;
	}
	
	public static void register(ModDamage routineUtility)
	{
		Routine.registerBase(LiteralRange.class, Pattern.compile("range\\." + Routine.dynamicIntegerPart + "\\." + Routine.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
}
