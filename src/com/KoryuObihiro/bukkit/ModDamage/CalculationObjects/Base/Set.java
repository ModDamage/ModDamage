package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;


import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Set extends ModDamageCalculation 
{
	private int setValue;
	public Set(int value){ setValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = setValue;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = setValue;}
}
