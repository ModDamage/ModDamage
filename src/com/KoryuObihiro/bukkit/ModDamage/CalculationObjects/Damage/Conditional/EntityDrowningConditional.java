package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityDrowningConditional extends EntityConditionalCalculation 
{
	public EntityDrowningConditional(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage) 
	{
		if((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getRemainingAir() == 0)
			return calculate(eventInfo, eventDamage);
		return eventDamage;
	}
}
