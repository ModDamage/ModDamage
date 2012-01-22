package com.ModDamage.Routines.Nested.Conditionals;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.MaterialAliaser;

public class EntityBlockStatus extends Conditional
{
	public static final Pattern pattern = Pattern.compile("(\\w+)\\.is(\\w+)block\\.(\\w+)", Pattern.CASE_INSENSITIVE);
	
	final EntityReference entityReference;
	final BlockStatusType statusType;
	final Collection<Material> materials;
	protected EntityBlockStatus(EntityReference entityReference, BlockStatusType statusType, Collection<Material> materials)
	{
		this.entityReference = entityReference;
		this.statusType = statusType;
		this.materials = materials;
	}
	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		if(entityReference.getEntity(eventInfo) != null)
			return statusType.isTrue(materials, entityReference.getEntity(eventInfo));
		return false;
	}
	
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
	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.SimpleConditionalBuilder
	{
		public ConditionalBuilder() { super(pattern); }

		@Override
		public EntityBlockStatus getNew(Matcher matcher)
		{
			BlockStatusType statusType = null;
			for(BlockStatusType type : BlockStatusType.values())
				if(matcher.group(2).equalsIgnoreCase(type.name()))
						statusType = type;
			Collection<Material> materials = MaterialAliaser.match(matcher.group(3));
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null && statusType != null)
				return new EntityBlockStatus(reference, statusType, materials);
			return null;
		}
	}

}
