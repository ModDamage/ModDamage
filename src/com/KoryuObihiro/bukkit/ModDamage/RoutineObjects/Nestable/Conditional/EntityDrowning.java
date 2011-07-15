package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

public class EntityDrowning extends EntityAirTicksComparison 
{
	public EntityDrowning(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, 0, ComparisonType.LESS_THAN_EQUALS);
	}
	
	public static void register()
	{
		ConditionalRoutine.registerStatement(EntityDrowning.class, Pattern.compile("drowning", Pattern.CASE_INSENSITIVE));
	}
}
