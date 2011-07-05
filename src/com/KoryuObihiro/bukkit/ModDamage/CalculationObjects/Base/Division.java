package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Division implements ModDamageCalculation 
{
	private int divideValue;
	public Division(int value){ divideValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = eventInfo.eventDamage/divideValue;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = eventInfo.eventHealth/divideValue;}
}
