package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityAltitudeComparison extends EntityComparison
{
	public EntityAltitudeComparison(boolean inverted, boolean forAttacker, int altitude, ComparisonType comparisonType, List<ModDamageCalculation> calculations)
	{
		super(inverted, forAttacker, altitude, comparisonType, calculations);
	}
	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().getBlockY();}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().getBlockY();}
}
