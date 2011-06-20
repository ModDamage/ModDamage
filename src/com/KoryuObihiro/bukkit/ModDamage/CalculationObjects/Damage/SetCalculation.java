package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

public class SetCalculation extends DamageCalculation 
{
	private int setValue;
	public SetCalculation(int value){ setValue = value;}
	@Override
	public int calculate(int eventDamage){ return setValue;}
}
