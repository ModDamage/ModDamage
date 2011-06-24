package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityReflect extends EntityDamageEffectCalculation 
{
	final List<DamageCalculation> calculations;
	public EntityReflect(List<DamageCalculation> calculations)
	{
		forAttacker = true;
		this.calculations = calculations;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		int originalDamage = eventInfo.eventDamage;
		for(DamageCalculation calculation : calculations)
			calculation.calculate(eventInfo);
		eventInfo.entity_attacker.damage(eventInfo.eventDamage);//Don't use an attacking entity here because of the nature of this mechanic.
		eventInfo.eventDamage = originalDamage;
	}
}
