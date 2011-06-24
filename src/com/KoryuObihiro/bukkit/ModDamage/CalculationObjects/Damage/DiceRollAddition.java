package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;

public class DiceRollAddition extends ChanceDamageCalculation 
{
	private int maxValue;
	public DiceRollAddition(int value){ maxValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage += Math.abs(random.nextInt()%(maxValue + 1));}
}