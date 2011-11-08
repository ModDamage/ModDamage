package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Multiplication extends Routine 
{
	private int multiplicationValue;
	public Multiplication(String configString, int value)
	{ 
		super(configString);
		multiplicationValue = value;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue *= multiplicationValue;}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("mult(?:iply)?\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{	
		@Override
		public Multiplication getNew(Matcher matcher)
		{ 
			return new Multiplication(matcher.group(), Integer.parseInt(matcher.group(1)));
		}
	}
}
