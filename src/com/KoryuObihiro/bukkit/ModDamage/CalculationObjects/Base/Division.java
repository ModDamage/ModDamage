package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;


import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Division extends ModDamageCalculation 
{
	private int divideValue;
	public Division(int value){ divideValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = eventInfo.eventDamage/divideValue;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = eventInfo.eventHealth/divideValue;}
	
	public static void register()
	{
		CalculationUtility.register(Division.class, Pattern.compile("div\\.([0-9]*)", Pattern.CASE_INSENSITIVE));
	}
}
