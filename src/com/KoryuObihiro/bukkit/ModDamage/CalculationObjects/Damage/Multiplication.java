package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class Multiplication extends DamageCalculation 
{
	private int multiplicationValue;
	public Multiplication(int value){ multiplicationValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage *= multiplicationValue;}
}
