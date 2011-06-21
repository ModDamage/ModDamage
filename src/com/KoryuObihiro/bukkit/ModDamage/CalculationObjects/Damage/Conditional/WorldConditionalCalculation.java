package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import org.bukkit.World;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public abstract class WorldConditionalCalculation extends ConditionalDamageCalculation
{
	protected boolean isWorldConditional = true;
	World world;
	protected int calculate(int eventDamage)
	{
		int result = eventDamage;
		for(DamageCalculation damageCalculation : calculations)
			result = damageCalculation.calculate(result);
		return result;
	}
}
