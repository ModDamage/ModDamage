package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityLightComparison extends EntityComparison
{
	public EntityLightComparison(boolean inverted, boolean forAttacker, int lightLevel, ComparisonType comparisonType, List<ModDamageCalculation> calculations) 
	{
		super(inverted, forAttacker, lightLevel, comparisonType, calculations);
	}

	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getLocation().getBlock().getLightLevel();}

	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getLocation().getBlock().getLightLevel();}
}
