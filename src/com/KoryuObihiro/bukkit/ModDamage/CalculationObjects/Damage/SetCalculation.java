package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class SetCalculation extends DamageCalculation 
{
	private int setValue;
	public SetCalculation(int value){ setValue = value;}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage){ return setValue;}
}
