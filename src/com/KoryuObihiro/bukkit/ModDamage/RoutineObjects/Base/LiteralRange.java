package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class LiteralRange extends Chanceroutine 
{
	private DynamicInteger lowerBound, upperBound;
	public LiteralRange(String configString, DynamicInteger lower, DynamicInteger upper)
	{ 
		super(configString);
		lowerBound = lower;
		upperBound = upper;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = lowerBound.getValue(eventInfo) + Math.abs(random.nextInt()%(upperBound.getValue(eventInfo) - lowerBound.getValue(eventInfo) + 1));}
	
	public static void register()
	{
		Routine.registerBase(LiteralRange.class, Pattern.compile("range\\." + DynamicInteger.dynamicPart + "\\." + DynamicInteger.dynamicPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static LiteralRange getNew(Matcher matcher)
	{ 
		if(matcher != null)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1)), match2 = DynamicInteger.getNew(matcher.group(2));
			if(match1 != null && match2 != null)
				return new LiteralRange(matcher.group(), match1, match2);
		}
		return null;
	}
}
