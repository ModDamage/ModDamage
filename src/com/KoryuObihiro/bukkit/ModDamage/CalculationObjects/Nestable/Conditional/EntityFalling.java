package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;

public class EntityFalling extends EntityFallComparison 
{
	public EntityFalling(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, 3, ComparisonType.GREATER_THAN_EQUALS);
	}
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(EntityFalling.class, Pattern.compile(CalculationUtility.entityPart + "falling", Pattern.CASE_INSENSITIVE));
	}
}
