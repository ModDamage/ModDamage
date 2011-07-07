package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Multiplication extends ModDamageCalculation 
{
	private int multiplicationValue;
	public Multiplication(int value){ multiplicationValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage *= multiplicationValue;}
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth *= multiplicationValue;}
}
