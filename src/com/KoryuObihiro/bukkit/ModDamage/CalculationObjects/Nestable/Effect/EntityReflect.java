package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Effect;


import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityReflect extends EntityEffectCalculation
{
	public EntityReflect(List<ModDamageCalculation> calculations)
	{
		super(true, calculations);
	}
	
	@Override
	void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.getWorld().createExplosion(affectedObject.getLocation(), input);
	}
	
	@Override
	protected LivingEntity getAffectedObject(SpawnEventInfo eventInfo){ return null;}
	
	@Override
	protected int calculateInputValue(DamageEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventDamage, temp2;
		eventInfo.eventDamage = 0;
		doCalculations(eventInfo);
		temp2 = eventInfo.eventDamage;
		eventInfo.eventDamage = temp1;
		return temp2;
	}

	@Override
	protected int calculateInputValue(SpawnEventInfo eventInfo){ return 0;}
	
	public static void register()
	{
		CalculationUtility.register(EntityReflect.class, Pattern.compile("effect\\.reflect", Pattern.CASE_INSENSITIVE));
	}
}
