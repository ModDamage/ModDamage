package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntitySetHealth extends EntityEffectCalculation<Integer>
{
	public EntitySetHealth(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		super(forAttacker, calculations);
	}
	
	public EntitySetHealth(boolean forAttacker, int power)
	{
		super(forAttacker, power);
	}

	@Override
	void applyEffect(LivingEntity affectedObject, Integer input) 
	{
		affectedObject.setHealth(input);
	}
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
	protected Integer calculateInputValue(SpawnEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventHealth, temp2;
		eventInfo.eventHealth = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventHealth;
		eventInfo.eventHealth = temp1;
		return temp2;
	}
}
