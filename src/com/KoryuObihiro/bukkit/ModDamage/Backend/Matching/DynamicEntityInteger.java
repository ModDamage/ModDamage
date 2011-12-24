package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicEntityInteger extends DynamicInteger
{
	protected final EntityReference entityReference;
	private final EntityIntegerPropertyMatch propertyMatch;
	public enum EntityIntegerPropertyMatch
	{
		AirTicks(true, ModDamageElement.LIVING)
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
		FallDistance(false, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return (int) ((LivingEntity)entity).getFallDistance();
			}
		},
		FireTicks(true, ModDamageElement.LIVING)
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
		Health(true, ModDamageElement.LIVING)
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
		Light
		{
			@Override
			public int getValue(Entity entity)
			{
				return entity.getLocation().getBlock().getLightLevel();
			}
		},
		NoDamageTicks(false, ModDamageElement.LIVING)
		{
			@Override
			public int getValue(Entity entity)
			{
				return ((LivingEntity)entity).getNoDamageTicks();
			}
		},
		Size(true, ModDamageElement.SLIME)
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
	
	DynamicEntityInteger(EntityReference reference, EntityIntegerPropertyMatch propertyMatch, boolean isNegative)
	{
		super(isNegative, propertyMatch.settable);
		this.entityReference = reference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(propertyMatch.requiredElement))
			return (isNegative?-1:1) * propertyMatch.getValue(entityReference.getEntity(eventInfo));
		return 0;//Shouldn't happen.
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value)
	{
		if(propertyMatch.settable && entityReference.getElement(eventInfo).matchesType(propertyMatch.requiredElement))
			propertyMatch.setValue(entityReference.getEntity(eventInfo), value);
	}
	
	@Override
	public String toString()
	{
		return isNegative?"-":"" + entityReference.name().toLowerCase() + "_" + propertyMatch.name().toLowerCase();
	}
}