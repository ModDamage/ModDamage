package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Binomial extends ConditionalStatement
{
	private final Random random = new Random();
	private final int chance;
	public Binomial(int value)
	{ 
		super(false);
		chance = (value <= 0?100:value);
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return Math.abs(random.nextInt()%101) <= chance;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, Binomial.class, Pattern.compile("binom\\." + Routine.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static Binomial getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Binomial(Integer.parseInt(matcher.group(1)));
		return null;
	}
}
