package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.AliasManager;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

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
	final Collection<Material> materials;
	protected EntityBlockStatus(boolean inverted, EntityReference entityReference, BlockStatusType statusType, Collection<Material> materials)
	{
		super(inverted, entityReference);
		this.statusType = statusType;
		this.materials = materials;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo)
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
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)(.*)\\.is(\\w+)block\\.(.*)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public EntityBlockStatus getNew(Matcher matcher)
		{
			BlockStatusType statusType = null;
			for(BlockStatusType type : BlockStatusType.values())
				if(matcher.group(3).equalsIgnoreCase(type.name()))
						statusType = type;
			Collection<Material> materials = AliasManager.matchMaterialAlias(matcher.group(4));
			EntityReference reference = EntityReference.match(matcher.group(2));
			if(reference != null && statusType != null)
				return new EntityBlockStatus(matcher.group(1).equalsIgnoreCase("!"), reference, statusType, materials);
			return null;
		}
	}

}
