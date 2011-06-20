package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

public class DivisionCalculation extends DamageCalculation 
{
	private int divideValue;
	public DivisionCalculation(int value){ divideValue = value;}
	@Override
	public int calculate(int eventDamage){ return eventDamage/divideValue;}
}
