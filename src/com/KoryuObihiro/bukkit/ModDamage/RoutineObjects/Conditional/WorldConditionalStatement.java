package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;


public abstract class WorldConditionalStatement extends ConditionalStatement
{
	protected final boolean useEventWorld;
	protected final World world;
	public WorldConditionalStatement(boolean inverted, World world) 
	{
		super(inverted);
		this.world = world;
		useEventWorld = false;
	}
	
	public WorldConditionalStatement(boolean inverted)
	{
		super(inverted);
		this.world = null;
		useEventWorld = true;
	}
}
