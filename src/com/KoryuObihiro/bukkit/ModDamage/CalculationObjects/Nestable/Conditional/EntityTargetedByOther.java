package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityTargetedByOther extends EntityConditionalCalculation<LivingEntity>
{
	LivingEntity value;
	public EntityTargetedByOther(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{ 
		super(inverted, forAttacker, null, calculations);
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo)
	{ 
		value = (forAttacker?eventInfo.entity_target:eventInfo.entity_attacker);
		return ((Creature)getRelevantEntity(eventInfo)).getTarget().equals(value);
	}
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
	
	@Override
	protected LivingEntity getRelevantInfo(DamageEventInfo eventInfo){ return null;}
	@Override
	protected LivingEntity getRelevantInfo(SpawnEventInfo eventInfo){ return null;}
	
	public static void register()
	{
		CalculationUtility.register(EntityTargetedByOther.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "targetedbyother", Pattern.CASE_INSENSITIVE));
	}
}
