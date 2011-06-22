package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;

public class DiceRollCalculation extends ChanceCalculation 
{
	public DiceRollCalculation(){}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage){ return Math.abs(random.nextInt()%(eventDamage + 1));}
}