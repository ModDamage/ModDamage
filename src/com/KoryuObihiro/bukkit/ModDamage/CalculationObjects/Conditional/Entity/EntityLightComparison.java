package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityLightComparison extends EntityAttributeComparison 
{
	final ComparisonUtility comparisonType;
	public EntityLightComparison(boolean inverted, boolean forAttacker, ComparisonUtility comparisonType, byte lightLevel, List<ModDamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.value = lightLevel;
		this.comparisonType = comparisonType;
		this.forAttacker = forAttacker;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return ComparisonUtility.compare(comparisonType, (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getLightLevel(), value);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return ComparisonUtility.compare(comparisonType, eventInfo.entity.getLocation().getBlock().getLightLevel(), value);}
}
