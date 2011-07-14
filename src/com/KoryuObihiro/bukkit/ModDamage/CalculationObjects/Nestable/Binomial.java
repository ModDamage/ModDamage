package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DiceRoll;

public class Binomial extends NestedCalculation
{
	private final Random random = new Random();
	private final int chance;
	public Binomial(int value, List<ModDamageCalculation> calculations)
	{ 
		super(calculations);
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
	
	public static Binomial getNew(Matcher matcher, List<ModDamageCalculation> calculations)
	{ 
		if(matcher != null)
			return new Binomial(Integer.parseInt(matcher.group(1)), calculations);
		return null;
	}
	
	
	public static void register()
	{
		CalculationUtility.register(DiceRoll.class, Pattern.compile("binom\\.([0-9]{1-2})", Pattern.CASE_INSENSITIVE));
	}
}
