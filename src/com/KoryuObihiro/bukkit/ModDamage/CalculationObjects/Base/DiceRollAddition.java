package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

public class DiceRollAddition extends ChanceCalculation 
{
	private int maxValue;
	public DiceRollAddition(int value){ maxValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage += Math.abs(random.nextInt()%(maxValue + 1));}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth += Math.abs(random.nextInt()%(maxValue + 1));}
}