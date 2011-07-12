package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityDrowning extends EntityAirTicksComparison 
{
	public EntityDrowning(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{  
		super(inverted, forAttacker, 0, ComparisonType.LESS_THAN_EQUALS, calculations);
	}
	
	public static void register()
	{
		CalculationUtility.register(EntityDrowning.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "drowning", Pattern.CASE_INSENSITIVE));
	}
}
