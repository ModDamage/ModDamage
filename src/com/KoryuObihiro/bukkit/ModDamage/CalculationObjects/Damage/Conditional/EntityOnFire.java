package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityOnFire extends EntityConditionalCalculation 
{
	public EntityOnFire(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public void calculate(EventInfo eventInfo) 
	{
		if((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getFireTicks() > 0)
			makeCalculations(eventInfo);
	}
}
