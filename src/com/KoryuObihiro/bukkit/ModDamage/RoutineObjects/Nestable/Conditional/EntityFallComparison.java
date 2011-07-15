package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

public class EntityFallComparison extends EntityComparison 
{
	public EntityFallComparison(boolean inverted, boolean forAttacker, int fallDistance, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, fallDistance, comparisonType);
	}

	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getFallDistance();}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getFallDistance();}
	
	public static void register()
	{
		ConditionalRoutine.registerStatement(EntityFallComparison.class, Pattern.compile(RoutineUtility.entityPart + "falldistance" + RoutineUtility.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
