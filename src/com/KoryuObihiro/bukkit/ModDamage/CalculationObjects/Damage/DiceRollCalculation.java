package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import org.bukkit.entity.LivingEntity;

public class DiceRollCalculation extends ChanceCalculation 
{
	public DiceRollCalculation(){}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage){ return Math.abs(random.nextInt()%(eventDamage + 1));}
}