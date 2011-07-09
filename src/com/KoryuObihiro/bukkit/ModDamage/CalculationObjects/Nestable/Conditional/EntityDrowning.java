package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityDrowning extends EntityAirTicksComparison 
{
	public EntityDrowning(boolean inverted, boolean forAttacker, List<ModDamageCalculation> calculations)
	{  
		super(inverted, forAttacker, 0, ComparisonType.LESS_THAN_EQUALS, calculations);
	}
}
