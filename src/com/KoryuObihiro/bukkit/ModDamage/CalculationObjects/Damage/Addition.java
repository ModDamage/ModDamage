package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class Addition extends DamageCalculation 
{
	private int addValue;
	public Addition(int value){ addValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage += addValue;}
}
