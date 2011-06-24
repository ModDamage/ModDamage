package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityHealthNotEquals extends EntityConditionalCalculation 
{
	int value;
	public EntityHealthNotEquals(boolean forAttacker, int compareTo, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.value = compareTo;
		this.calculations = calculations;
	}
	@Override
	public void calculate(EventInfo eventInfo) 
	{
		if((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getHealth() != value)
			calculate(eventInfo);
	}
}
