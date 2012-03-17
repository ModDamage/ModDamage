package com.ModDamage.Variables.Strings;

import org.bukkit.World;

import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.StringExp;

public class WorldString extends StringExp
{
	public enum WorldStringProperty
	{
		EVENT_WORLD
		{
			@Override
			protected String getString(World world)
			{
				return world.getName();
			}
		},
		EVENT_ENVIRONMENT
		{
			@Override
			protected String getString(World world)
			{
				return world.getEnvironment().name();
			}
		};
		
		protected String getString(World world){ return null; }
	}
	

	private final WorldStringProperty worldProperty;
	private final DataRef<World> worldRef;
	
	private WorldString(WorldStringProperty dynamicProperty, DataRef<World> worldRef)
	{
		this.worldProperty = dynamicProperty;
		this.worldRef = worldRef;
	}
	
	public String getString(EventData data)
	{
		World world = worldRef.get(data);
		return worldProperty.getString(world);
	}
	
	public static WorldString getNew(String string, EventInfo info)
	{
		DataRef<World> worldRef = info.get(World.class, "world");
		if (worldRef == null) return null;
		
		try
		{
			return new WorldString(WorldStringProperty.valueOf(string.toUpperCase()), worldRef);
		}
		catch (IllegalArgumentException e)
		{
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		return worldProperty == null? "null" : worldProperty.name().toLowerCase();
	}
}