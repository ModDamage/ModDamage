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
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Routines.Nested.Conditional;

public class EntityStatus extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.is("+ Utils.joinBy("|", StatusType.values()) +")", Pattern.CASE_INSENSITIVE);
	final EntityReference entityReference;
	protected static final double magic_MinFallSpeed = 0.1d;
	private static final List<Material> waterList = Arrays.asList(Material.WATER, Material.STATIONARY_WATER);

	final StatusType statusType;
	private enum StatusType
	{
		
		/*Blocking(ModDamageElement.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).is() <= 0;}
		},FIXME Get this into Bukkit if not already present.*/
		Drowning(ModDamageElement.LIVING)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((LivingEntity)entity).getRemainingAir() <= 0;}
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
			public boolean isTrue(Entity entity){ return entity.getVelocity().getY() > magic_MinFallSpeed;}
		},
		OnFire
		{
			@Override
			public boolean isTrue(Entity entity){ return entity.getFireTicks() > 0;}
		},
		Sleeping(ModDamageElement.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).isSleeping();}
		},
		Sneaking(ModDamageElement.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).isSneaking();}
		},
		Sprinting(ModDamageElement.PLAYER)
		{
			@Override
			public boolean isTrue(Entity entity){ return ((Player)entity).isSprinting();}
		},
		Underwater
		{
			@Override
			public boolean isTrue(Entity entity)
			{
				return waterList.contains(entity.getLocation().getBlock().getType()) && (entity instanceof LivingEntity)?waterList.contains(((LivingEntity)entity).getEyeLocation().getBlock().getType()):true;
			}
		};
		
		protected ModDamageElement requiredElement = ModDamageElement.GENERIC;
		StatusType(){}
		StatusType(ModDamageElement requiredElement)
		{
			this.requiredElement = requiredElement;
		}
		
		abstract public boolean isTrue(Entity entity);
	}
	
	protected EntityStatus(EntityReference entityReference, StatusType statusType)
	{
		this.entityReference = entityReference;
		this.statusType = statusType;
	}

	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		ModDamageElement element = entityReference.getElement(eventInfo);
		Entity entity = entityReference.getEntity(eventInfo);
		if(element != null && entity != null && element.matchesType(statusType.requiredElement))
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
		public EntityStatus getNew(Matcher matcher)
		{
			if(matcher != null)
			{
				StatusType statusType = null;
				for(StatusType type : StatusType.values())
					if(matcher.group(2).equalsIgnoreCase(type.name()))
							statusType = type;
				EntityReference reference = EntityReference.match(matcher.group(1));
				if(reference != null && statusType != null)
					return new EntityStatus(reference, statusType);
			}
			return null;
		}
	}
}
