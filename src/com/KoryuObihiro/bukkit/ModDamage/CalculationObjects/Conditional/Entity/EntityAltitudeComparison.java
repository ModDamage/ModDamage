package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityAltitudeComparison extends EntityAttributeComparison
{
	final ComparisonType comparisonType;
	public EntityAltitudeComparison(boolean inverted, boolean forAttacker, ComparisonType comparisonType, int altitude, List<ModDamageCalculation> calculations)
	{
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.comparisonType = comparisonType;
		this.value = altitude;
		this.calculations = calculations;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return CalculationUtility.compare(comparisonType, (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlockY(), value);}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo){ return CalculationUtility.compare(comparisonType, eventInfo.entity.getLocation().getBlockY(), value);}
}
