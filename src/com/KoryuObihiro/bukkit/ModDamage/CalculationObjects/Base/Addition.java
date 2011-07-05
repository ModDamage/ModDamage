package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Addition implements ModDamageCalculation 
{
	private int addValue;
	public Addition(int value){ addValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage += addValue;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth += addValue;}
}
