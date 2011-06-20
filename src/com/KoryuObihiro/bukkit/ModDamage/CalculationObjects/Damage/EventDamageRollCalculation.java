package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

public class EventDamageRollCalculation extends ChanceCalculation 
{
	@Override
	public int calculate(int eventDamage){ return Math.abs(random.nextInt()%(eventDamage + 1));}
}