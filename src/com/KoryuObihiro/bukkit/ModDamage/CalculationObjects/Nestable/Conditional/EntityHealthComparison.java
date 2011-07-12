package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
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
	
	public static void register()
	{
		CalculationUtility.register(EntityHealthComparison.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "health" + CalculationUtility.comparisonPart + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
