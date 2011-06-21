package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

public class DiceRollCalculation extends ChanceCalculation 
{
	public DiceRollCalculation(){}
	@Override
	public int calculate(int eventDamage){ return Math.abs(random.nextInt()%(eventDamage + 1));}
}