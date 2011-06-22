package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityHealthGreaterThanEquals extends EntityConditionalCalculation 
{
	int value;
	public EntityHealthGreaterThanEquals(boolean forAttacker, int compareTo, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.value = compareTo;
		this.calculations = calculations;
	}
	@Override
	public int calculate(EventInfo eventInfo, int eventDamage) 
	{
		if((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getHealth() >= value)
			return calculate(eventInfo, eventDamage);
		return eventDamage;
	}
}
