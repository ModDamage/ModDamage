package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

public class DivisionAdditionCalculation extends DamageCalculation 
{
	private int divideValue;
	public DivisionAdditionCalculation(int value){ divideValue = (value != 0?value:1);}
	@Override
	public int calculate(int eventDamage){ return eventDamage/divideValue;}
}
