package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base.DiceRoll;

public class Binomial extends NestedRoutine
{
	private final Random random = new Random();
	private final int chance;
	public Binomial(int value, List<Routine> calculations)
	{ 
		super(calculations);
		chance = (value <= 0?100:value);
	}
	@Override
	public void run(DamageEventInfo eventInfo) 
	{
		if(Math.abs(random.nextInt()%101) <= chance)
			doCalculations(eventInfo);
	}
	@Override
	public void run(SpawnEventInfo eventInfo) 
	{
		if(Math.abs(random.nextInt()%101) <= chance)
			doCalculations(eventInfo);
	}
	
	public static Binomial getNew(Matcher matcher, List<Routine> calculations)
	{ 
		if(matcher != null)
			return new Binomial(Integer.parseInt(matcher.group(1)), calculations);
		return null;
	}
	
	
	public static void register()
	{
		RoutineUtility.register(DiceRoll.class, Pattern.compile("binom\\.([0-9]{1,2})", Pattern.CASE_INSENSITIVE));
	}
}
