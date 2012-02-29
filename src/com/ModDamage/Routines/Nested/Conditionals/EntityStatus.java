package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.ModDamage.ModDamage;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.EntityType;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class EntityStatus extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.is("+ Utils.joinBy("|", StatusType.values()) +")", Pattern.CASE_INSENSITIVE);
	protected static final double magic_MinFallSpeed = 0.1d;
	private static final List<Material> waterList = Arrays.asList(Material.WATER, Material.STATIONARY_WATER);

	private final DataRef<Entity> entityRef;
	private final DataRef<EntityType> entityElementRef;
	private final StatusType statusType;
	private enum StatusType
	{
		
		/*Blocking(ModDamageElement.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).is() <= 0; }
		},FIXME Get this into Bukkit if not already present.*/
		Drowning(EntityType.LIVING)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((LivingEntity)entity).getRemainingAir() <= 0; }
		},
		ExposedToSky
		{
			@Override
			public boolean isTrue(Entity entity)
			{
				int i = entity.getLocation().getBlockX();
				int k = entity.getLocation().getBlockZ();
				for(int j = (entity instanceof LivingEntity?((LivingEntity)entity).getEyeLocation():entity.getLocation()).getBlockY(); j < 128; j++)
					if(!ModDamage.goThroughThese.contains(entity.getWorld().getBlockAt(i, j, k).getType())) return false;
				return true;
			}
		},
		Falling
		{
			@Override
			public boolean isTrue(Entity entity){ return entity.getVelocity().getY() > magic_MinFallSpeed; }
		},
		OnFire
		{
			@Override
			public boolean isTrue(Entity entity){ return entity.getFireTicks() > 0; }
		},
		Sleeping(EntityType.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).isSleeping(); }
		},
		Sneaking(EntityType.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).isSneaking(); }
		},
		Sprinting(EntityType.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).isSprinting(); }
		},
		Underwater
		{
			@Override
			public boolean isTrue(Entity entity)
			{
				return waterList.contains(entity.getLocation().getBlock().getType()) && (entity instanceof LivingEntity)?waterList.contains(((LivingEntity)entity).getEyeLocation().getBlock().getType()):true;
			}
		};
		
		protected EntityType requiredElement = EntityType.ENTITY;
		StatusType(){}
		StatusType(EntityType requiredElement)
		{
			this.requiredElement = requiredElement;
		}
		
		abstract public boolean isTrue(Entity entity);
	}
	
	protected EntityStatus(DataRef<Entity> entityRef, DataRef<EntityType> entityElementRef, StatusType statusType)
	{
		this.entityRef = entityRef;
		this.entityElementRef = entityElementRef;
		this.statusType = statusType;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		EntityType element = entityElementRef.get(data);
		Entity entity = entityRef.get(data);
		if(element != null && entity != null && element.matches(statusType.requiredElement))
			return statusType.isTrue(entity);
		return false;
	}
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityStatus getNew(Matcher matcher, EventInfo info)
		{
			if(matcher != null)
			{
				StatusType statusType = null;
				for(StatusType type : StatusType.values())
					if(matcher.group(2).equalsIgnoreCase(type.name()))
							statusType = type;
				String name = matcher.group(1).toLowerCase();
				DataRef<Entity> entityRef = info.get(Entity.class, name);
				DataRef<EntityType> entityElementRef = info.get(EntityType.class, name);
				if(entityRef != null && statusType != null)
					return new EntityStatus(entityRef, entityElementRef, statusType);
			}
			return null;
		}
	}
}
