package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class BinomialCalculation extends ChanceCalculation 
{
	private DamageCalculation damageCalculation;
	public BinomialCalculation(int value)
	{ 
		chance = value;
		if(chance < 0 || chance > 100) chance = 100;
		damageCalculation = new SetCalculation(0);
	}
	public BinomialCalculation(int value, DamageCalculation calculation)
	{ 
		chance = value;
		if(chance < 0 || chance > 100) chance = 100;
		damageCalculation = calculation;
	}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage)
		{ return ((Math.abs(random.nextInt()%101) <= chance)?damageCalculation.calculate(target, attacker, eventDamage):eventDamage);}
}
