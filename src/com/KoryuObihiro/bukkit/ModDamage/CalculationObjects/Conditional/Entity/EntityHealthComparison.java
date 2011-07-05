package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityHealthComparison extends EntityAttributeComparison
{
	final ComparisonType comparisonType;
	public EntityHealthComparison(boolean inverted, boolean forAttacker, ComparisonType comparisonType, int compareTo, List<ModDamageCalculation> calculations)
	{ 
		this.inverted = inverted;
		this.forAttacker = forAttacker;
		this.comparisonType = comparisonType;
		this.value = compareTo;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return CalculationUtility.compare(comparisonType, (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getHealth(), value);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return CalculationUtility.compare(comparisonType, eventInfo.entity.getHealth(), value);}
}
