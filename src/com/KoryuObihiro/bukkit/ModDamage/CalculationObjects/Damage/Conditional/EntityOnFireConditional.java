package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityOnFireConditional extends EntityConditionalCalculation 
{
	public EntityOnFireConditional(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage) 
	{
		if((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getFireTicks() > 0)
			return makeCalculations(eventInfo, eventDamage);
		return 0;
	}
}
