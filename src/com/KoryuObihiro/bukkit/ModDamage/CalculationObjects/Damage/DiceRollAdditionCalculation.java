package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

public class DiceRollAdditionCalculation extends ChanceCalculation 
{
	private int maxValue;
	public DiceRollAdditionCalculation(int value){ maxValue = value;}
	@Override
	public int calculate(int eventDamage){ return eventDamage + Math.abs(random.nextInt()%(maxValue + 1));}
}