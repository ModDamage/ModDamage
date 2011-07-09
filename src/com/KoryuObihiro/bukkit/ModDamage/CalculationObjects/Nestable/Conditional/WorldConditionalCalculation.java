package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;


public abstract class WorldConditionalCalculation extends ConditionalCalculation
{
	protected final boolean useEventWorld;
	protected final World world;
	public WorldConditionalCalculation(boolean inverted, World world, List<ModDamageCalculation> calculations) 
	{
		super(inverted, calculations);
		this.world = world;
		useEventWorld = false;
	}
	
	public WorldConditionalCalculation(boolean inverted, List<ModDamageCalculation> calculations)
	{
		super(inverted, calculations);
		this.world = null;
		useEventWorld = true;
	}
}
