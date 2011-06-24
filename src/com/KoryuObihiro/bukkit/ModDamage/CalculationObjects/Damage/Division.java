package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class Division extends DamageCalculation 
{
	private int divideValue;
	public Division(int value){ divideValue = value;}
	@Override
	public void calculate(EventInfo eventInfo){ eventInfo.eventDamage = eventInfo.eventDamage/divideValue;}
}
