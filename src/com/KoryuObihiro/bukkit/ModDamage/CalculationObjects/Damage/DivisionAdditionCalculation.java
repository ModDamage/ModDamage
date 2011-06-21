package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class DivisionAdditionCalculation extends DamageCalculation 
{
	private int divideValue;
	public DivisionAdditionCalculation(int value){ divideValue = (value != 0?value:1);}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage){ return eventDamage/divideValue;}
}
