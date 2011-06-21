package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class MultiplicationCalculation extends DamageCalculation 
{
	private int multiplicationValue;
	public MultiplicationCalculation(int value){ multiplicationValue = value;}
	@Override
	public int calculate(int eventDamage){ return eventDamage * multiplicationValue;}
}
