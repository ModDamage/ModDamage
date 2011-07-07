package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;


import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityReflect extends EntityEffectCalculation 
{
	public EntityReflect(List<ModDamageCalculation> calculations)
	{
		forAttacker = true;
		this.calculations = calculations;
	}
	
	public EntityReflect(int damageBack)
	{
		forAttacker = true;
		this.value = damageBack;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{ 
		if(calculations != null)
		{
			value = eventInfo.eventDamage;
			doCalculations(eventInfo);
			eventInfo.entity_attacker.damage(eventInfo.eventDamage, eventInfo.entity_attacker);
			eventInfo.eventDamage = value;
		}
		else eventInfo.entity_attacker.damage(value);
	}
	@Override
	public void calculate(SpawnEventInfo eventInfo){}
}
