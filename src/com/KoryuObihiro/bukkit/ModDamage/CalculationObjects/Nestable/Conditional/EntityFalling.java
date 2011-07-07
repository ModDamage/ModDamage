package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityFalling extends EntityFallComparison 
{
	public EntityFalling(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{  
		super(inverted, forAttacker, 3, ComparisonType.GREATER_THAN_EQUALS, calculations);
	}
}
