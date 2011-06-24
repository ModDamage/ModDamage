package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;

public class DiceRoll extends ChanceDamageCalculation 
{
	public DiceRoll(){}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = Math.abs(random.nextInt()%(eventInfo.eventDamage + 1));}
}