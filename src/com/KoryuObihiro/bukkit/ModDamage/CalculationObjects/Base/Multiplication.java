package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;


import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Multiplication extends ModDamageCalculation 
{
	private int multiplicationValue;
	public Multiplication(int value){ multiplicationValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage *= multiplicationValue;}
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth *= multiplicationValue;}
	
	public static void register()
	{
		CalculationUtility.register(Multiplication.class, Pattern.compile("mult\\.([0-9]*)", Pattern.CASE_INSENSITIVE));
	}
}
