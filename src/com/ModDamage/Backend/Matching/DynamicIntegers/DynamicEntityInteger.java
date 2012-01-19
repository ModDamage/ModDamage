package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;

public class DynamicEntityInteger extends DynamicInteger
{
	protected final EntityReference entityReference;
	private final EntityIntegerPropertyMatch propertyMatch;
	public enum EntityIntegerPropertyMatch
	{
		AIRTICKS(true, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getRemainingAir();
			}
			
			@Override
			public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setRemainingAir(value);
			}
		},
		FALLDISTANCE(false, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return (int) ((LivingEntity)entity).getFallDistance();
			}
		},
		FIRETICKS(true, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getFireTicks();
			}
			
			@Override
			public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setFireTicks(value);
			}
		},
		HEALTH(true, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getHealth();
			}
			
			@Override
			public void setValue(Entity entity, int value)
			{
				((LivingEntity)entity).setHealth(value);
			}
		},
		LIGHT
		{
			@Override
			public int getValue(Entity entity)
			{
				return entity.getLocation().getBlock().getLightLevel();
			}
		},
		MAXHEALTH(false, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getMaxHealth();
			}
		},
		NODAMAGETICKS(false, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getNoDamageTicks();
			}
		},
		SIZE(true, ModDamageElement.SLIME)
		{
			@Override
			public int getValue(Entity entity)
			{
				return (entity instanceof Slime?((Slime)entity).getSize():0);
			}
			
			@Override
			public void setValue(Entity entity, int value)
			{
				if(entity instanceof Slime) ((Slime)entity).setSize(value);
			}
		},
		X
		{
			@Override
			public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockX();
			}
		},
		Y
		{
			@Override
			public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockY();
			}
		},
		Z
		{
			@Override
			public int getValue(Entity entity)
			{
				return entity.getLocation().getBlockZ();
			}
		};
		
		public boolean settable = false;
		private ModDamageElement requiredElement = ModDamageElement.GENERIC;
		private EntityIntegerPropertyMatch(){}
		private EntityIntegerPropertyMatch(boolean settable)
		{
			this.settable = settable;
		}
		private EntityIntegerPropertyMatch(boolean settable, ModDamageElement requiredElement)
		{
			this.settable = settable;
			this.requiredElement = requiredElement;
		}
		
		abstract public int getValue(Entity entity);
		public void setValue(Entity entity, int value){}
	}
	
	public static void register()
	{
		DynamicInteger.register(
				Pattern.compile("("+ Utils.joinBy("|", EntityReference.values()) +")_("+ 
									 Utils.joinBy("|", EntityIntegerPropertyMatch.values()) +")", Pattern.CASE_INSENSITIVE),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher matcher, StringMatcher sm)
					{
						sm.accept();
						return new DynamicEntityInteger(
								EntityReference.valueOf(matcher.group(1).toUpperCase()), 
								EntityIntegerPropertyMatch.valueOf(matcher.group(2).toUpperCase()));
					}
				});
	}
	
	DynamicEntityInteger(EntityReference reference, EntityIntegerPropertyMatch propertyMatch)
	{
		this.entityReference = reference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		ModDamageElement element = entityReference.getElement(eventInfo);
		Entity entity = entityReference.getEntity(eventInfo);
		
		if(element != null && entity != null && element.matchesType(propertyMatch.requiredElement))
			return propertyMatch.getValue(entity);
		
		return 0; //Shouldn't happen.
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		if(!propertyMatch.settable) return;
		
		ModDamageElement element = entityReference.getElement(eventInfo);
		if (element != null && element.matchesType(propertyMatch.requiredElement))
			propertyMatch.setValue(entityReference.getEntity(eventInfo), value);
	}
	
	@Override
	public boolean isSettable()
	{
		return propertyMatch.settable;
	}
	
	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "_" + propertyMatch.name().toLowerCase();
	}
}