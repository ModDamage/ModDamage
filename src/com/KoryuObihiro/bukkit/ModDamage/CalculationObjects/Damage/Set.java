package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class Set extends DamageCalculation 
{
	private int setValue;
	public Set(int value){ setValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = setValue;}
}
