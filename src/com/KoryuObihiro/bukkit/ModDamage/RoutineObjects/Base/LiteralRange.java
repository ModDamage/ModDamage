package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class LiteralRange extends Chanceroutine 
{
	private IntegerMatch lowerBound, upperBound;
	public LiteralRange(String configString, IntegerMatch lower, IntegerMatch upper)
	{ 
		super(configString);
		lowerBound = lower;
		upperBound = upper;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = lowerBound.getValue(eventInfo) + Math.abs(random.nextInt()%(upperBound.getValue(eventInfo) - lowerBound.getValue(eventInfo) + 1));}
	
	public static void register()
	{
		Routine.registerBase(LiteralRange.class, Pattern.compile("range\\." + Routine.dynamicIntegerPart + "\\." + Routine.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static LiteralRange getNew(Matcher matcher)
	{ 
		if(matcher != null)
		{
			IntegerMatch match1 = IntegerMatch.getNew(matcher.group(1)), match2 = IntegerMatch.getNew(matcher.group(2));
			if(match1 != null && match2 != null)
				return new LiteralRange(matcher.group(), match1, match2);
		}
		return null;
	}
}
