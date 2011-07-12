package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.DiceRoll;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base.Set;

public class Binomial extends ConditionalCalculation
{
	private final Random random = new Random();
	private final int chance;
	public Binomial(int value)
	{ 
		super(false, Arrays.asList((ModDamageCalculation)new Set(0)));
		chance = value;
	}
	public Binomial(int value, List<ModDamageCalculation> calculations)
	{ 
		super(false, calculations);
		if(value < 0 || value > 100) chance = 100;
		else chance = value;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo) 
	{
		return Math.abs(random.nextInt()%101) <= chance;
	}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo) 
	{
		return Math.abs(random.nextInt()%101) <= chance;
	}
	
	public static void register()
	{
		CalculationUtility.register(DiceRoll.class, Pattern.compile("binom\\.([0-9]*)", Pattern.CASE_INSENSITIVE));
	}
}
