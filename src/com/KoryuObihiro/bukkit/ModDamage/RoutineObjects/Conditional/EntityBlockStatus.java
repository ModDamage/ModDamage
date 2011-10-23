package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityBlockStatus extends EntityConditionalStatement
{
	public static final HashSet<Material> goThroughThese = new HashSet<Material>();
	
	static
	{
		goThroughThese.add(Material.AIR );
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
	
	final BlockStatusType statusType;
	final HashSet<Material> materials;
	protected EntityBlockStatus(boolean inverted, EntityReference entityReference, BlockStatusType statusType, HashSet<Material> materials)
	{
		super(inverted, entityReference);
		this.statusType = statusType;
		this.materials = materials;
	}
	@Override
	protected boolean condition(TargetEventInfo eventInfo)
	{
		if(entityReference.getEntity(eventInfo) != null)
			return statusType.isTrue(materials, entityReference.getEntity(eventInfo));
		return false;
	}
	
	private enum BlockStatusType
	{
		OnBlock
		{
			@Override
			public boolean isTrue(HashSet<Material> materials, Entity entity)
			{
				return materials.contains(entity.getLocation().add(0, -1, 0).getBlock().getType());
			}
		},
		OverBlock
		{
			@Override
			public boolean isTrue(HashSet<Material> materials, Entity entity)
			{
				return searchVertically(false, materials, entity);
			}
		},
		UnderBlock
		{
			@Override
			public boolean isTrue(HashSet<Material> materials, Entity entity)
			{
				return searchVertically(true, materials, entity);
			}
		};
		
		abstract public boolean isTrue(HashSet<Material> materials, Entity entity);
		
		private static boolean searchVertically(boolean goingUp, HashSet<Material> materials, Entity entity)
		{
			//TODO Indefinite - Integrate into a special library. :D
			Material thisMaterial = null;
			int i = entity.getLocation().getBlockX();
			int k = entity.getLocation().getBlockZ();
			if(goingUp)
				for(int j = (entity instanceof LivingEntity?((LivingEntity)entity).getEyeLocation():entity.getLocation()).getBlockY(); j < 128; j++)
				{
					thisMaterial = entity.getWorld().getBlockAt(i, j, k).getType();
					if(materials.contains(thisMaterial))
						return true;
					else if(!goThroughThese.contains(thisMaterial))
						break;
				}
			else
				for(int j = (entity instanceof LivingEntity?((LivingEntity)entity).getEyeLocation():entity.getLocation()).getBlockY(); j > 0; j--)
				{
					thisMaterial = entity.getWorld().getBlockAt(i, j, k).getType();
					if(materials.contains(thisMaterial))
						return true;
					else if(!goThroughThese.contains(thisMaterial))
						break;
				}
			return false;
		}
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityBlockStatus.class, Pattern.compile("(!?)(\\w+)\\.is(\\w+)block\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityBlockStatus getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			BlockStatusType statusType = null;
			for(BlockStatusType type : BlockStatusType.values())
				if(matcher.group(3).equalsIgnoreCase(type.name()))
						statusType = type;
			HashSet<Material> materials = new HashSet<Material>(ModDamage.matchMaterialAlias(matcher.group(4)));
			if(EntityReference.isValid(matcher.group(2)) && statusType != null)
				return new EntityBlockStatus(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)), statusType, materials);
		}
		return null;
	}

}
