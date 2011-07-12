package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityAirTicksComparison extends EntityComparison
{
	public EntityAirTicksComparison(boolean inverted, boolean forAttacker, int ticks, ComparisonType comparisonType, List<ModDamageCalculation> calculations)
	{ 
		super(inverted, forAttacker, ticks, comparisonType, calculations);
	}
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getRemainingAir();}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.entity.getRemainingAir();}
	
	public static void register()
	{
		CalculationUtility.register(EntityAirTicksComparison.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "airticks" + CalculationUtility.comparisonPart + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
