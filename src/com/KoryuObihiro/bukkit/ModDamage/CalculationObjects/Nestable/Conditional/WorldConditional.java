package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;


public abstract class WorldConditional extends ConditionalCalculation
{
	protected final boolean useEventWorld;
	protected final World world;
	public WorldConditional(boolean inverted, World world, List<ModDamageCalculation> calculations) 
	{
		super(inverted, calculations);
		useEventWorld = (world == null);
		this.world = world;
	}
}
