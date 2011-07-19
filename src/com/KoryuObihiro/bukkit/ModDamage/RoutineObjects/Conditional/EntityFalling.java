package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

public class EntityFalling extends EntityFallComparison 
{
	public EntityFalling(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, 3, ComparisonType.GREATER_THAN_EQUALS);
	}
	
	public static void register(RoutineUtility routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityFalling.class, Pattern.compile(RoutineUtility.entityPart + "falling", Pattern.CASE_INSENSITIVE));
	}
}
