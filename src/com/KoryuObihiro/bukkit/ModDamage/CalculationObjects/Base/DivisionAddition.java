package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class DivisionAddition extends ModDamageCalculation 
{
	private int divideValue;
	public DivisionAddition(int value){ divideValue = (value != 0?value:1);}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage += eventInfo.eventDamage/divideValue;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth += eventInfo.eventHealth/divideValue;}
}
