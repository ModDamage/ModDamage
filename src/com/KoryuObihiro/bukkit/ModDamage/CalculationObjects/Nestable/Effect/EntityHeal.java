package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityHeal extends EntityEffectCalculation
{
	public EntityHeal(boolean forAttacker, List<ModDamageCalculation> calculations)
	{
		super(forAttacker, calculations);
	}

	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setHealth(affectedObject.getHealth() + input);
	}
	
	@Override
	protected int calculateInputValue(DamageEventInfo eventInfo) 
	{
		eventInfo.eventDamage = 0;
		doCalculations(eventInfo);
		int temp = eventInfo.eventDamage;
		eventInfo.eventDamage = 0;
		return temp;
	}
	
	@Override
	protected int calculateInputValue(SpawnEventInfo eventInfo) 
	{
		eventInfo.eventHealth = 0;
		doCalculations(eventInfo);
		int temp = eventInfo.eventHealth;
		eventInfo.eventHealth = 0;
		return temp;
	}
	
	public static void register()
	{
		CalculationUtility.register(EntityHeal.class, Pattern.compile(CalculationUtility.entityPart + "effect\\.heal", Pattern.CASE_INSENSITIVE));
	}
}
