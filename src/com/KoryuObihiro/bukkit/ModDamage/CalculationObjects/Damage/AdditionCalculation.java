package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import org.bukkit.entity.LivingEntity;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class AdditionCalculation extends DamageCalculation 
{
	private int addValue;
	public AdditionCalculation(int value){ addValue = value;}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage){ return eventDamage + addValue;}
}
