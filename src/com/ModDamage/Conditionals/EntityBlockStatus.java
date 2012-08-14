package com.ModDamage.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Alias.MaterialAliaser;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class EntityBlockStatus extends Conditional<Entity>
{
	public static final Pattern pattern = Pattern.compile("\\.is(\\w+)(?:block)?\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	
	private enum BlockStatusType
	{
		ON
		{
			@Override
			public boolean isTrue(Collection<Material> materials, Entity entity)
			{
				return materials.contains(entity.getLocation().add(0, -1, 0).getBlock().getType());
			}
		},
		IN
		{
			@Override
			public boolean isTrue(Collection<Material> materials, Entity entity)
			{
				if (entity instanceof LivingEntity &&
						materials.contains(((LivingEntity)entity).getEyeLocation().getBlock().getType()))
					return true;
				return materials.contains(entity.getLocation().getBlock().getType());
			}
		},
		STANDINGIN
		{
			@Override
			public boolean isTrue(Collection<Material> materials, Entity entity)
			{
				return materials.contains(entity.getLocation().getBlock().getType());
			}
		},
		BREATHING
		{
			@Override
			public boolean isTrue(Collection<Material> materials, Entity entity)
			{
				if (entity instanceof LivingEntity &&
						materials.contains(((LivingEntity)entity).getEyeLocation().getBlock().getType()))
					return true;
				return false;
			}
		},
		OVER
		{
			@Override
			public boolean isTrue(Collection<Material> materials, Entity entity)
			{
				return searchVertically(false, materials, entity);
			}
		},
		UNDER
		{
			@Override
			public boolean isTrue(Collection<Material> materials, Entity entity)
			{
				return searchVertically(true, materials, entity);
			}
		};
		
		abstract public boolean isTrue(Collection<Material> materials, Entity entity);
		
		private static boolean searchVertically(boolean goingUp, Collection<Material> materials, Entity entity)
		{
			Material thisMaterial = null;
			int i = entity.getLocation().getBlockX();
			int k = entity.getLocation().getBlockZ();
			if(goingUp)
				for(int j = (entity instanceof LivingEntity?((LivingEntity)entity).getEyeLocation():entity.getLocation()).getBlockY(); j < 128; j++)
				{
					thisMaterial = entity.getWorld().getBlockAt(i, j, k).getType();
					if(materials.contains(thisMaterial))
						return true;
					else if(!ModDamage.goThroughThese.contains(thisMaterial))
						break;
				}
			else
				for(int j = (entity instanceof LivingEntity?((LivingEntity)entity).getEyeLocation():entity.getLocation()).getBlockY(); j > 0; j--)
				{
					thisMaterial = entity.getWorld().getBlockAt(i, j, k).getType();
					if(materials.contains(thisMaterial))
						return true;
					else if(!ModDamage.goThroughThese.contains(thisMaterial))
						break;
				}
			return false;
		}
	}
	
	
	private final BlockStatusType statusType;
	private final Collection<Material> materials;
	
	protected EntityBlockStatus(IDataProvider<Entity> entityDP, BlockStatusType statusType, Collection<Material> materials)
	{
		super(Entity.class, entityDP);
		this.statusType = statusType;
		this.materials = materials;
	}
	@Override
	public Boolean get(Entity entity, EventData data)
	{
		return statusType.isTrue(materials, entity);
	}
	
	@Override
	public String toString()
	{
		return startDP + ".is" + statusType.name().toLowerCase() + "." + Utils.joinBy(",", materials);
	}
	
	
	public static void register()
	{
		DataProvider.register(Boolean.class, Entity.class, pattern, new IDataParser<Boolean, Entity>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Class<?> want, IDataProvider<Entity> entityDP, Matcher m, StringMatcher sm)
				{
					BlockStatusType statusType = null;
					for(BlockStatusType type : BlockStatusType.values())
						if(m.group(1).equalsIgnoreCase(type.name()))
								statusType = type;
					if(statusType == null) return null;
					
					Collection<Material> materials = MaterialAliaser.match(m.group(2));
					
					return new EntityBlockStatus(entityDP, statusType, materials);
				}
			});
	}
}
