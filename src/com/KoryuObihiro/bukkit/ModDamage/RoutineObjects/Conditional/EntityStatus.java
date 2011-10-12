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
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityStatus extends EntityConditionalStatement
{
	static final List<Material> waterList = Arrays.asList(Material.WATER, Material.STATIONARY_WATER);

	public static final HashSet<Material> goThroughThese = new HashSet<Material>();
	
	static
	{
		goThroughThese.add(Material.AIR );
		goThroughThese.add(Material.GLASS);
		goThroughThese.add(Material.LADDER);
		goThroughThese.add(Material.TORCH);
		goThroughThese.add(Material.REDSTONE_TORCH_ON);
		goThroughThese.add(Material.REDSTONE_TORCH_OFF);
		goThroughThese.add(Material.STATIONARY_LAVA);
		goThroughThese.add(Material.STONE_PLATE);
		goThroughThese.add(Material.STONE_BUTTON);
		goThroughThese.add(Material.SIGN_POST);
		goThroughThese.add(Material.WALL_SIGN);
		goThroughThese.add(Material.FIRE);
		goThroughThese.add(Material.LEVER);
	}
	
	final StatusType statusType;
	protected EntityStatus(boolean inverted, EntityReference entityReference, StatusType statusType)
	{
		super(inverted, entityReference);
		this.statusType = statusType;
	}

	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if((!statusType.forLiving && entity instanceof LivingEntity) && (!statusType.forPlayer && (entity instanceof Player)))
		{
			switch(statusType)
			{
				case Drowning:
					return entityReference.getEntity(eventInfo) instanceof LivingEntity && ((LivingEntity)entityReference.getEntity(eventInfo)).getRemainingAir() <= 0;
				case ExposedToSky:
					int i = entity.getLocation().getBlockX();
					int k = entity.getLocation().getBlockZ();
					for(int j = ((entity instanceof LivingEntity)?((LivingEntity)entity).getEyeLocation():entity.getLocation()).getBlockY(); j < 128; j++)
						if(!goThroughThese.contains(eventInfo.world.getBlockAt(i, j, k).getType())) return false;
					return true;
				case Falling:
					return entityReference.getEntity(eventInfo).getFallDistance() > 3;
				case OnFire:
					return entity.getFireTicks() > 0;
				case Sleeping:
					return ((Player)entity).isSleeping();
				case Sneaking:
					return ((Player)entity).isSneaking();
				case Underwater:
					return waterList.contains(entityReference.getEntity(eventInfo).getLocation().getBlock().getType()) && (entityReference.getEntity(eventInfo) instanceof LivingEntity)?waterList.contains(((LivingEntity)entityReference.getEntity(eventInfo)).getEyeLocation().getBlock().getType()):true;
						
			}
		}
		return false;
	}
	
	private enum StatusType
	{
		Drowning(true, false),
		ExposedToSky,
		Falling,
		OnFire,
		Sleeping(false, true),
		Sneaking(false, true),
		Underwater;
		
		protected boolean forLiving = false;
		protected boolean forPlayer = false;
		StatusType(){}
		StatusType(boolean forLiving,boolean forPlayer)
		{
			this.forLiving = forLiving;
			this.forPlayer = forPlayer;
		}
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
