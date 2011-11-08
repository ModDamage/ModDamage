package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class LiteralRange extends RandomRoutine 
{
	private DynamicInteger lowerBound, upperBound;
	public LiteralRange(String configString, DynamicInteger lower, DynamicInteger upper)
	{ 
		super(configString);
		lowerBound = lower;
		upperBound = upper;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue += lowerBound.getValue(eventInfo) + Math.abs(random.nextInt()%(upperBound.getValue(eventInfo) - lowerBound.getValue(eventInfo) + 1));}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("range\\." + DynamicInteger.dynamicIntegerPart + "\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{	
		@Override
		public LiteralRange getNew(Matcher matcher)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1)), match2 = DynamicInteger.getNew(matcher.group(2));
			if(match1 != null && match2 != null)
				return new LiteralRange(matcher.group(), match1, match2);
			return null;
		}
	}
}
