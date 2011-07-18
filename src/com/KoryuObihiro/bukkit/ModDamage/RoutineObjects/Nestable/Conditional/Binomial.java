package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

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
	public boolean condition(DamageEventInfo eventInfo){ return Math.abs(random.nextInt()%101) <= chance;}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return Math.abs(random.nextInt()%101) <= chance;}
	
	public static Binomial getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Binomial(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register(RoutineUtility routineUtility)
	{
		ConditionalRoutine.registerNested(routineUtility, Binomial.class, Pattern.compile("binom\\.([0-9]{1,2})", Pattern.CASE_INSENSITIVE));
	}
}
