package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;

public class DiceRollAddition extends ChanceCalculation 
{
	private int maxValue;
	public DiceRollAddition(int value){ maxValue = value;}
	@Override
	public void calculate(EventInfo eventInfo){ eventInfo.eventDamage += Math.abs(random.nextInt()%(maxValue + 1));}
}