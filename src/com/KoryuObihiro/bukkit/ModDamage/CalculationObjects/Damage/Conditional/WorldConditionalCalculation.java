package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage.Conditional;

import org.bukkit.World;

public abstract class WorldConditionalCalculation extends ConditionalDamageCalculation
{
	protected boolean isWorldConditional = true;
	World world;
}
