package com.ModDamage.Conditionals;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;

public class EntityStatus extends Conditional<Entity>
{
	public static final Pattern pattern = Pattern.compile("\\.is("+ Utils.joinBy("|", StatusType.values()) +")", Pattern.CASE_INSENSITIVE);
	protected static final double magic_MinFallSpeed = 0.1d;
	private static final List<Material> waterList = Arrays.asList(Material.WATER, Material.STATIONARY_WATER);

	private enum StatusType
	{
		ExposedToSky
		{
			@Override
			public boolean isTrue(Entity entity)
			{
				Location loc = entity.getLocation();
				if (entity instanceof LivingEntity)
					loc = ((LivingEntity)entity).getEyeLocation();
				int i = loc.getBlockX();
				int k = loc.getBlockZ();
				int worldHeight = entity.getWorld().getMaxHeight();
				for(int j = loc.getBlockY(); j < worldHeight; j++)
					if(!ModDamage.goThroughThese.contains(entity.getWorld().getBlockAt(i, j, k).getType()))
						return false;
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
		Underwater
		{
			@Override
			public boolean isTrue(Entity entity)
			{
				if (!waterList.contains(entity.getLocation().getBlock().getType()))
					return false;
				if (entity instanceof LivingEntity)
					if (!waterList.contains(((LivingEntity)entity).getEyeLocation().getBlock().getType()))
						return false;
				return true;
			}
		};
		
		abstract public boolean isTrue(Entity entity);
	}

	private final StatusType statusType;
	
	protected EntityStatus(IDataProvider<Entity> entityDP, StatusType statusType)
	{
		super(Entity.class, entityDP);
		this.statusType = statusType;
	}

	@Override
	public Boolean get(Entity entity, EventData data)
	{
		return statusType.isTrue(entity);
	}
	
	@Override
	public String toString()
	{
		return startDP + ".is" + statusType.name().toLowerCase();
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean, Entity>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
				{
					StatusType statusType = null;
					for(StatusType type : StatusType.values())
						if(m.group(1).equalsIgnoreCase(type.name()))
								statusType = type;
					if(statusType == null) return null;
					
					return new EntityStatus(entityDP, statusType);
				}
			});
	}
}
