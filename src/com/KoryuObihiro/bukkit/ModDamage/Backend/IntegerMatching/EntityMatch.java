package com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class EntityMatch extends IntegerMatch
{
	protected final EntityReference reference;
	protected final EntityPropertyMatch propertyMatch;
	enum EntityPropertyMatch implements MatcherEnum
	{
		AirTicks(true),
		FallDistance(true),
		FireTicks(true),
		Health(true),
		Light,
		Size,
		X,
		Y,
		Z;
		//TODO Add player stuff
		
		private boolean castsToLiving;
		private EntityPropertyMatch()
		{
			this.castsToLiving = false;
		}
		private EntityPropertyMatch(boolean castsToLiving)
		{
			this.castsToLiving = castsToLiving;
		}
		
		protected int getProperty(TargetEventInfo eventInfo, EntityReference reference)
		{
			Entity entity = reference.getEntity(eventInfo);
			if(!castsToLiving || (entity instanceof LivingEntity))
				switch(this)
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
			return 0;//TODO 0.9.6 - Do we really want this behavior?
		}
	}
	
	EntityMatch(EntityReference reference, EntityPropertyMatch propertyMatch)
	{
		this.reference = reference;
		this.propertyMatch = propertyMatch;
	}
	
	@Override
	public long getValue(TargetEventInfo eventInfo){ return propertyMatch.getProperty(eventInfo, reference);}
}
