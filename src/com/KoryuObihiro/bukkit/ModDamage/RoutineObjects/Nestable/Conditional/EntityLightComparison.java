package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

public class EntityLightComparison extends EntityComparison
{
	public EntityLightComparison(boolean inverted, boolean forAttacker, int lightLevel, ComparisonType comparisonType) 
	{
		super(inverted, forAttacker, lightLevel, comparisonType);
	}

	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getLocation().getBlock().getLightLevel();}

	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return (int)getRelevantEntity(eventInfo).getLocation().getBlock().getLightLevel();}
	
	public static void register()
	{
		ConditionalRoutine.registerStatement(EntityLightComparison.class, Pattern.compile(RoutineUtility.entityPart + "light" + RoutineUtility.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
