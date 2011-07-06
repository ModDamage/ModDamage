package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityAltitudeComparison extends EntityAttributeComparison
{
	final ComparisonUtility comparisonType;
	public EntityAltitudeComparison(boolean inverted, boolean forAttacker, ComparisonUtility comparisonType, int altitude, List<ModDamageCalculation> calculations)
	{
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.comparisonType = comparisonType;
		this.value = altitude;
		this.calculations = calculations;
	}
	@Override
	protected boolean condition(DamageEventInfo eventInfo){ return ComparisonUtility.compare(comparisonType, (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlockY(), value);}
	@Override
	protected boolean condition(SpawnEventInfo eventInfo){ return ComparisonUtility.compare(comparisonType, eventInfo.entity.getLocation().getBlockY(), value);}
}
