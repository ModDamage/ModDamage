package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicEntityInteger extends DynamicInteger
{
	public enum EntityIntegerPropertyMatch
	{
		AIRTICKS(true, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getRemainingAir();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setRemainingAir(value);
			}
		},
		FALLDISTANCE(false, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return (int) ((LivingEntity)entity).getFallDistance();
			}
		},
		FIRETICKS(true, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getFireTicks();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setFireTicks(value);
			}
		},
		HEALTH(true, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getHealth();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setHealth(value);
			}
		},
		LIGHT
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlock().getLightLevel();
			}
		},
		MAXHEALTH(false, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getMaxHealth();
			}
		},
		NODAMAGETICKS(false, EntityType.LIVING)
		{
			@Override public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getNoDamageTicks();
			}
		},
		SIZE(true, EntityType.SLIME)
		{
			@Override public int getValue(Entity entity)
			{
				return (entity instanceof Slime?((Slime)entity).getSize():0);
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				if(entity instanceof Slime) ((Slime)entity).setSize(value);
			}
		},
		X(true)
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockX();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				entity.getLocation().setX(value);
			}
		},
		Y(true)
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockY();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				entity.getLocation().setY(value);
			}
		},
		Z(true)
		{
			@Override public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockZ();
			}
			
			@Override public void setValue(Entity entity, int value)
			{
				entity.getLocation().setZ(value);
			}
		};
		
		public boolean settable = false;
		private EntityType requiredElement = EntityType.ENTITY;
		private EntityIntegerPropertyMatch(){}
		private EntityIntegerPropertyMatch(boolean settable)
		{
			this.settable = settable;
		}
		private EntityIntegerPropertyMatch(boolean settable, EntityType requiredElement)
		{
			this.settable = settable;
			this.requiredElement = requiredElement;
		}
		
		abstract public int getValue(Entity entity);
		public void setValue(Entity entity, int value){}
	}
	
	protected final DataRef<Entity> entityRef;
	protected final DataRef<EntityType> entityElementRef;
	private final EntityIntegerPropertyMatch propertyMatch;
	
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("([a-z]+)_("+ Utils.joinBy("|", EntityIntegerPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm, EventInfo info)
					{
						String name = matcher.group(1).toLowerCase();
						DataRef<Entity> entityRef = info.get(Entity.class, name);
						DataRef<EntityType> entityElementRef = info.get(EntityType.class, name);
						if (entityRef == null || entityElementRef == null) return null;
						
						return sm.acceptIf(new DynamicEntityInteger(
								entityRef, entityElementRef,
								EntityIntegerPropertyMatch.valueOf(matcher.group(2).toUpperCase())));
					}
				});
	}
	
	DynamicEntityInteger(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, EntityIntegerPropertyMatch propertyMatch)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(EventData data)
	{
		Entity entity = entityRef.get(data);
		EntityType element = entityElementRef.get(data);
		
		if(element != null && entity != null && element.matches(propertyMatch.requiredElement))
			return propertyMatch.getValue(entity);
		
		return 0; //Shouldn't happen.
	}
	
	@Override
	public void setValue(EventData data, int value)
	{
		if(!propertyMatch.settable) return;
		
		Entity entity = entityRef.get(data);
		EntityType element = entityElementRef.get(data);
		
		if (element != null && element.matches(propertyMatch.requiredElement))
			propertyMatch.setValue(entity, value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString()
	{
		return entityRef + "_" + propertyMatch.name().toLowerCase();
	}
}