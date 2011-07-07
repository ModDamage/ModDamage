package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityHealthComparison extends EntityComparison
{
	public EntityHealthComparison(boolean inverted, boolean forAttacker, int health, ComparisonType comparisonType, List<ModDamageCalculation> calculations)
	{ 
		super(inverted, forAttacker, health, comparisonType, calculations);
	}
	
	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return getRelevantEntity(eventInfo).getHealth();}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return getRelevantEntity(eventInfo).getHealth();}
}
