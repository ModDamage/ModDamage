package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityExplode extends EntityEffectCalculation<Float>
{
	public EntityExplode(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		super(forAttacker, calculations);
	}
	
	public EntityExplode(boolean forAttacker, float power)
	{
		super(forAttacker, power);
	}

	@Override
	void applyEffect(LivingEntity affectedObject, Float input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), input);
	}
	
	@Override
	protected Float calculateInputValue(DamageEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventDamage;
		float temp2;
		eventInfo.eventDamage = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventDamage;
		eventInfo.eventDamage = temp1;
		return temp2;
	}
	@Override
	protected Float calculateInputValue(SpawnEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventHealth;
		float temp2;
		eventInfo.eventHealth = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventHealth;
		eventInfo.eventHealth = temp1;
		return temp2;
	}
}
