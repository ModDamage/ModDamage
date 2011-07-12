package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityCoordinateComparison extends EntityComparison
{
	public EntityCoordinateComparison(boolean inverted, boolean forAttacker, int altitude, ComparisonType comparisonType, List<ModDamageCalculation> calculations)
	{
		super(inverted, forAttacker, altitude, comparisonType, calculations);
	}
	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().getBlockX();}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return getRelevantEntity(eventInfo).getLocation().getBlockX();}
	
	public static void register()
	{
		CalculationUtility.register(EntityCoordinateComparison.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "(X|Y|Z)\\." + CalculationUtility.comparisonPart + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
