package com.ModDamage.Variables.Ints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class TagInt extends IntegerExp
{	
	public static void register()
	{
		IntegerExp.register(
				Pattern.compile("([a-z]+)_tag(?:value)?_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						String name = matcher.group(1).toLowerCase();
						DataRef<Entity> entityRef = null;
						DataRef<EntityType> entityElementRef = null;
						DataRef<World> worldRef = null;
						if (!name.equals("world"))
						{
							entityRef = info.get(Entity.class, name);
							entityElementRef = info.get(EntityType.class, name);
							if (entityRef == null || entityElementRef == null) return null;
						}
						else
						{
							worldRef = info.get(World.class, name);
							if (worldRef == null)
							{
								ModDamage.addToLogRecord(OutputPreset.FAILURE, "This event does not have a world, so you cannot use world tags here.");
								return null;
							}
						}
						
						return sm.acceptIf(new TagInt(
								entityRef, entityElementRef, worldRef,
								matcher.group(2).toLowerCase()));
					}
				});
	}
	
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	protected final DataRef<World> worldRef;
	protected final String tag;
	
	TagInt(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, DataRef<World> worldRef, String tag)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.worldRef = worldRef;
		this.tag = tag;
	}
	
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		if (worldRef != null)
		{
			World world = worldRef.get(data);
			Integer value = ModDamage.getTagger().getTagValue(world, tag);
			if (value != null)
				return value;
		}
		else
		{
			Entity entity = entityRef.get(data);
			Integer value = ModDamage.getTagger().getTagValue(entity, tag);
			if (value != null)
				return value;
		}
		return 0;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		if (worldRef != null)
		{
			ModDamage.getTagger().addTag(worldRef.get(data), tag, value);
		}
		else
		{
			ModDamage.getTagger().addTag(entityRef.get(data), tag, value);
		}
		
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return (entityRef == null? worldRef : entityRef) + "_tag_" + tag;
	}
}