package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;

public class EntityOnFire extends EntityFireTicksComparison 
{
	public EntityOnFire(boolean inverted, boolean forAttacker)
	{  
		super(inverted, forAttacker, 3, ComparisonType.GREATER_THAN_EQUALS);
	}
	
	public static void register()
	{
		ConditionalCalculation.registerStatement(EntityOnFire.class, Pattern.compile(CalculationUtility.entityPart + "onfire", Pattern.CASE_INSENSITIVE));
	}
}
