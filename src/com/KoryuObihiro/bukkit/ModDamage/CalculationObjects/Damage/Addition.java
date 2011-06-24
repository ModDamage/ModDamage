package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class Addition extends DamageCalculation 
{
	private int addValue;
	public Addition(int value){ addValue = value;}
	@Override
	public void calculate(EventInfo eventInfo){ eventInfo.eventDamage += addValue;}
}
