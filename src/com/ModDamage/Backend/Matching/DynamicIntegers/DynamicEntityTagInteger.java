package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicEntityTagInteger extends DynamicInteger
{	
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("([a-z]+)_tag(?:value)?_(\\w+)", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						String name = matcher.group(1).toLowerCase();
						DataRef<Entity> entityRef = info.get(Entity.class, name);
						DataRef<EntityType> entityElementRef = info.get(EntityType.class, name);
						if (entityRef == null || entityElementRef == null) return null;
						
						return sm.acceptIf(new DynamicEntityTagInteger(
								entityRef, entityElementRef,
								matcher.group(2).toLowerCase()));
					}
				});
	}
	
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	protected final String tag;
	
	DynamicEntityTagInteger(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, String tag)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.tag = tag;
	}
	
	
	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		Entity entity = entityRef.get(data);
		Integer value = ModDamage.getTagger().getTagValue(entity, tag);
		if (value != null)
			return value;
		return 0;
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		ModDamage.getTagger().addTag(entityRef.get(data), tag, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return true;
	}
	
	@Override
	public String toString()
	{
		return entityRef + "_tag_" + tag;
	}
}