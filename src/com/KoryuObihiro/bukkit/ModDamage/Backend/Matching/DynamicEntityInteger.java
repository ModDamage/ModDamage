package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicEntityInteger extends DynamicInteger
{
	protected final EntityReference entityReference;
	private final EntityIntegerPropertyMatch propertyMatch;
	public enum EntityIntegerPropertyMatch
	{
		AirTicks(true, true),
		FallDistance(false, true),
		FireTicks(true, true),
		Health(true, true),
		Light,
		Size(true),
		X,
		Y,
		Z;
		
		public boolean settable = false;
		private boolean castsToLiving = false;
		private EntityIntegerPropertyMatch(){}
		private EntityIntegerPropertyMatch(boolean settable)
		{
			this.settable = settable;
		}
		private EntityIntegerPropertyMatch(boolean settable, boolean castsToLiving)
		{
			this.settable = settable;
			this.castsToLiving = castsToLiving;
		}
	}
	
	DynamicEntityInteger(EntityReference reference, EntityIntegerPropertyMatch propertyMatch)
	{
		super(propertyMatch.settable);
		this.entityReference = reference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public int getValue(TargetEventInfo eventInfo)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if((!propertyMatch.castsToLiving || (entity instanceof LivingEntity)))
			switch(propertyMatch)
			{
				case AirTicks: return ((LivingEntity)entity).getRemainingAir();
				case FallDistance: return (int) ((LivingEntity)entity).getFallDistance();
				case FireTicks: return ((LivingEntity)entity).getFireTicks();
				case Health: return ((LivingEntity)entity).getHealth();
				case Light: return entity.getLocation().getBlock().getLightLevel();
				case Size: if(entity instanceof Slime) return ((Slime)entity).getSize();
				case X: return entity.getLocation().getBlockX();
				case Y: return entity.getLocation().getBlockY();
				case Z: return entity.getLocation().getBlockZ();
			}
		return 0;//Shouldn't happen.
	}
	
	@Override
	public void setValue(TargetEventInfo eventInfo, int value, boolean additive)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if((!propertyMatch.castsToLiving || (entity instanceof LivingEntity)))
		{
			value += (additive?getValue(eventInfo):0);
			switch(propertyMatch)
			{
				case AirTicks: 		((LivingEntity)entity).setRemainingAir(value);
				case FireTicks:		((LivingEntity)entity).setFireTicks(value);
				case Health:		((LivingEntity)entity).setHealth(value);
				case Size: 			if(entity instanceof Slime) ((Slime)entity).setSize(value);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return entityReference.name().toLowerCase() + "." + propertyMatch.name().toLowerCase();
	}
}
