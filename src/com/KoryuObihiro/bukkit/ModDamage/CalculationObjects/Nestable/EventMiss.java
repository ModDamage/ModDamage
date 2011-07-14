package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Set;

public class EventMiss extends NestedCalculation
{
	private final Random random = new Random();
	private final int chance;
	public EventMiss(int value)
	{ 
		super(Arrays.asList((ModDamageCalculation)new Set(0)));
		chance = (value <= 0?100:value);
	}
	
	@Override
	public void calculate(DamageEventInfo eventInfo) 
	{
		if(Math.abs(random.nextInt()%101) <= chance)
			doCalculations(eventInfo);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo) 
	{
		if(Math.abs(random.nextInt()%101) <= chance)
			doCalculations(eventInfo);
	}
	
	public static EventMiss getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new EventMiss(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register()
	{
		CalculationUtility.register(DiceRoll.class, Pattern.compile("misschance\\.([0-9]{1-2})", Pattern.CASE_INSENSITIVE));
	}
}
