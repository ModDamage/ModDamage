package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityExposedToSky extends EntityConditionalStatement
{
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
			switch(world.getBlockAt(i, j, k).getType())
			{
				case AIR: 
				case TORCH: 
				case LADDER:
				case FIRE:
				case LEVER:
				case STONE_BUTTON:
				case WALL_SIGN:
				case GLASS: continue;
				default: return false;
			}
		return true;
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityExposedToSky.class, Pattern.compile("(!?)(\\w+)\\.exposedtosky", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityExposedToSky getNew(Matcher matcher)
	{
		if(matcher != null)
			if(EntityReference.isValid(matcher.group(2)))
				return new EntityExposedToSky(matcher.group(1).equalsIgnoreCase("!"), EntityReference.match(matcher.group(2)));
		return null;
	}

}
