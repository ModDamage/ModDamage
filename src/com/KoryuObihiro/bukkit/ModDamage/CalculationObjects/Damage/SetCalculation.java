package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class SetCalculation extends DamageCalculation 
{
	private int setValue;
	public SetCalculation(int value){ setValue = value;}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage){ return setValue;}
}
