package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityExposedToSky extends EntityConditionalStatement
{
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
	
	public EntityExposedToSky(boolean inverted, EntityReference entityReference) 
	{
		super(inverted, entityReference);
	}
	
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return isExposedToSky(entityReference.getEntity(eventInfo), eventInfo.world);}


	private boolean isExposedToSky(Entity entity, World world)
	{
		int i = entity.getLocation().getBlockX();
		int k = entity.getLocation().getBlockZ();
		for(int j = ((entity instanceof LivingEntity)?((LivingEntity)entity).getEyeLocation():entity.getLocation()).getBlockY(); j < 128; j++)
			if(!goThroughThese.contains(world.getBlockAt(i, j, k).getType())) return false;
		return true;
	}
	
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(EntityExposedToSky.class, Pattern.compile("(!?)(\\w+)\\.exposedtosky", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityExposedToSky getNew(Matcher matcher)
	{
		if(matcher != null)
			if(EntityReference.isValid(matcher.group(2)))
				return new EntityExposedToSky(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		return null;
	}

}
