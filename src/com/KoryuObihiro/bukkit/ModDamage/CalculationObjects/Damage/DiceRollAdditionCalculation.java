package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;

public class DiceRollAdditionCalculation extends ChanceCalculation 
{
	private int maxValue;
	public DiceRollAdditionCalculation(int value){ maxValue = value;}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage){ return eventDamage + Math.abs(random.nextInt()%(maxValue + 1));}
}