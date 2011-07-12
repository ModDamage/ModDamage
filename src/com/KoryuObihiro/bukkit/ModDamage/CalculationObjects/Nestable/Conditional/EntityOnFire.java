package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityOnFire extends EntityFireTicksComparison 
{
	public EntityOnFire(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{  
		super(inverted, forAttacker, 3, ComparisonType.GREATER_THAN_EQUALS, calculations);
	}
	
	public static void register()
	{
		CalculationUtility.register(EntityOnFire.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "onfire", Pattern.CASE_INSENSITIVE));
	}
}
