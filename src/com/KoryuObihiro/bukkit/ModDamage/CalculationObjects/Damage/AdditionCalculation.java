package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class AdditionCalculation extends DamageCalculation 
{
	private int addValue;
	public AdditionCalculation(int value){ addValue = value;}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage){ return eventDamage + addValue;}
}
