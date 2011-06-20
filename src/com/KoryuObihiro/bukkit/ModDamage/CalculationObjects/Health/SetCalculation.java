package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Health;

public class SetCalculation extends HealthCalculation 
{
	private int setValue;
	public SetCalculation(int value){ setValue = value;}
	@Override
	public int calculate(){ return setValue;}
}
