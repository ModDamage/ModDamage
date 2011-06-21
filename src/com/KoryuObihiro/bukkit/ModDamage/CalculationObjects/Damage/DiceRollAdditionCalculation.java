package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import org.bukkit.entity.LivingEntity;

public class DiceRollAdditionCalculation extends ChanceCalculation 
{
	private int maxValue;
	public DiceRollAdditionCalculation(int value){ maxValue = value;}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage){ return eventDamage + Math.abs(random.nextInt()%(maxValue + 1));}
}