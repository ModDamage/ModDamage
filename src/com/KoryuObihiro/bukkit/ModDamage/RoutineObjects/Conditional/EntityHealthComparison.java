package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

public class EntityHealthComparison extends EntityComparison
{
	public EntityHealthComparison(boolean inverted, boolean forAttacker, int health, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, health, comparisonType);
	}
	
	@Override
	protected Integer getRelevantInfo(DamageEventInfo eventInfo){ return getRelevantEntity(eventInfo).getHealth();}
	@Override
	protected Integer getRelevantInfo(SpawnEventInfo eventInfo){ return getRelevantEntity(eventInfo).getHealth();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityHealthComparison.class, Pattern.compile(ModDamage.entityPart + "health" + ModDamage.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
