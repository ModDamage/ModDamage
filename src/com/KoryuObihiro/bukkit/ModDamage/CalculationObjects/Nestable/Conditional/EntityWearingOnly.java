package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityWearingOnly extends EntityConditionalCalculation<String>
{
	public EntityWearingOnly(boolean inverted, boolean forAttacker, String armorSetString, List<ModDamageCalculation> calculations)
	{  
		super(forAttacker, forAttacker, armorSetString, calculations);
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo)
	{ 
		return ((forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target) != null) 
				?(forAttacker?eventInfo.armorSetString_attacker:eventInfo.armorSetString_target).equals(armorSetString)
				:false;
	}
	
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return false;}
	
	@Override
	protected LivingEntity getRelevantInfo(DamageEventInfo eventInfo){}
	@Override
	protected LivingEntity getRelevantInfo(SpawnEventInfo eventInfo){}
}
