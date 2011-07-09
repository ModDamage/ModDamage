package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;


import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityReflect extends EntityEffectCalculation<Integer>
{

	public EntityReflect(List<ModDamageCalculation> calculations)
	{
		super(true, calculations);
	}
	
	public EntityReflect(int power)
	{
		super(true, power);
	}

	@Override
	void applyEffect(LivingEntity affectedObject, Integer input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), input);
	}
	
	@Override
	protected LivingEntity getAffectedObject(SpawnEventInfo eventInfo){ return null;}
	
	@Override
	protected Integer calculateInputValue(DamageEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventDamage, temp2;
		eventInfo.eventDamage = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventDamage;
		eventInfo.eventDamage = temp1;
		return temp2;
	}

	@Override
	protected Integer calculateInputValue(SpawnEventInfo eventInfo){ return 0;}
}
