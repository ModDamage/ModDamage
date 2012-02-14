package com.ModDamage.Backend.Matching;

import org.bukkit.World;

import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicWorldString extends DynamicString
{
	public enum WorldProperty
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
	

	private final WorldProperty worldProperty;
	private final DataRef<World> worldRef;
	
	private DynamicWorldString(WorldProperty dynamicProperty, DataRef<World> worldRef)
	{
		this.worldProperty = dynamicProperty;
		this.worldRef = worldRef;
	}
	
	public String getString(EventData data)
	{
		World world = worldRef.get(data);
		return worldProperty.getString(world);
	}
	
	public static DynamicWorldString getNew(String string, EventInfo info)
	{
		DataRef<World> worldRef = info.get(World.class, "world");
		if (worldRef != null)
			try {
				return new DynamicWorldString(WorldProperty.valueOf(string.toUpperCase()), worldRef);
			}
			catch (IllegalArgumentException e) {}
		
		return null;
	}
	
	@Override
	public String toString()
	{
		return worldProperty == null? "null" : worldProperty.name().toLowerCase();
	}
}