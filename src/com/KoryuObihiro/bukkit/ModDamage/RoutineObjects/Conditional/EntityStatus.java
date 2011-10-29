package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityStatus extends EntityConditionalStatement
{
	private static final List<Material> waterList = Arrays.asList(Material.WATER, Material.STATIONARY_WATER);

	public static final HashSet<Material> goThroughThese = new HashSet<Material>();
	
	static
	{
		goThroughThese.add(Material.AIR );
		goThroughThese.add(Material.GLASS);
		goThroughThese.add(Material.LADDER);
		goThroughThese.add(Material.TORCH);
		goThroughThese.add(Material.REDSTONE_TORCH_ON);
		goThroughThese.add(Material.REDSTONE_TORCH_OFF);
		goThroughThese.add(Material.STONE_BUTTON);
		goThroughThese.add(Material.SIGN_POST);
		goThroughThese.add(Material.WALL_SIGN);
		goThroughThese.add(Material.FIRE);
		goThroughThese.add(Material.LEVER);
	}
	
	final StatusType statusType;
	private enum StatusType
	{
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
					if(!goThroughThese.contains(entity.getWorld().getBlockAt(i, j, k).getType())) return false;
				return true;
			}
		},
		Falling
		{
			@Override
			public boolean isTrue(Entity entity){ return entity.getFallDistance() > 0;}
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
	
	protected EntityStatus(boolean inverted, EntityReference entityReference, StatusType statusType)
	{
		super(inverted, entityReference);
		this.statusType = statusType;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		if(entityReference.getElement(eventInfo).matchesType(statusType.requiredElement))
			return statusType.isTrue(entityReference.getEntity(eventInfo));
		return false;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityStatus.class, Pattern.compile("(!?)(\\w+)\\.is(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityStatus getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			StatusType statusType = null;
			for(StatusType type : StatusType.values())
				if(matcher.group(3).equalsIgnoreCase(type.name()))
						statusType = type;
			if(EntityReference.isValid(matcher.group(2)) && statusType != null)
				return new EntityStatus(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), statusType);
		}
		return null;
	}
}
