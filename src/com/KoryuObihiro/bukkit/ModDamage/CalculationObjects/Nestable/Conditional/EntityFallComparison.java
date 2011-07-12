package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityFallComparison extends EntityComparison 
{
	public EntityFallComparison(boolean inverted, boolean forAttacker, int fallDistance, ComparisonType comparisonType, List<ModDamageCalculation> calculations)
	{ 
		super(inverted, forAttacker, fallDistance, comparisonType, calculations);
	}

	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getFallDistance();}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getFallDistance();}
	
	public static void register()
	{
		CalculationUtility.register(EntityFallComparison.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "falldistance" + CalculationUtility.comparisonPart + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
