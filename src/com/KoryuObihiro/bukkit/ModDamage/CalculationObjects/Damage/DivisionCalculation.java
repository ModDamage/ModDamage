package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class DivisionCalculation extends DamageCalculation 
{
	private int divideValue;
	public DivisionCalculation(int value){ divideValue = value;}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage){ return eventDamage/divideValue;}
}
