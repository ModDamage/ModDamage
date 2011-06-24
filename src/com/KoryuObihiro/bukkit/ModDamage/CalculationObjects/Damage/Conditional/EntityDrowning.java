package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import java.util.List;


import com.KoryuObihiro.bukkit.ModDamage.Backend.EventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityDrowning extends EntityConditionalCalculation 
{
	public EntityDrowning(boolean forAttacker, List<DamageCalculation> calculations)
	{ 
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public void calculate(EventInfo eventInfo) 
	{
		if((forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getRemainingAir() == 0)
			calculate(eventInfo);
	}
}
