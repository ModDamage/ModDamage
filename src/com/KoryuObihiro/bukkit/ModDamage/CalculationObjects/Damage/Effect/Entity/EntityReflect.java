package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Effect.Entity;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class EntityReflect extends EntityEffectDamageCalculation 
{
	int damageBack;
	public EntityReflect(List<DamageCalculation> calculations)
	{
		forAttacker = true;
		this.calculations = calculations;
	}
	
	public EntityReflect(int damageBack)
	{
		forAttacker = true;
		this.damageBack = damageBack;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			damageBack = eventInfo.eventDamage;
			for(DamageCalculation calculation : calculations)
				calculation.calculate(eventInfo);
			eventInfo.entity_attacker.damage(eventInfo.eventDamage, eventInfo.entity_attacker);
			eventInfo.eventDamage = damageBack;
		}
		else eventInfo.entity_attacker.damage(damageBack);
	}
}
