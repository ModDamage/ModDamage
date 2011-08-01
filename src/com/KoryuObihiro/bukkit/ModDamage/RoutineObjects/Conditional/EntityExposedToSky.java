package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityExposedToSky extends EntityConditionalStatement<Boolean>
{
	public EntityExposedToSky(boolean inverted, boolean forAttacker) 
	{
		super(inverted, forAttacker, true);
	}
	
	@Override
	protected boolean condition(TargetEventInfo eventInfo){ return isExposedToSky(getRelevantEntity(eventInfo), eventInfo.world);}

	@Override
	protected Boolean getRelevantInfo(TargetEventInfo eventInfo){ return false;}

	private boolean isExposedToSky(LivingEntity entity, World world)
	{
		int i = entity.getLocation().getBlockX();
		int k = entity.getLocation().getBlockZ();
		for(int j = entity.getLocation().getBlockY(); j < 128; j++)
			switch(world.getBlockAt(i, j, k).getType())
			{
				case AIR: 
				case TORCH: 
				case LADDER:
				case FIRE:
				case LEVER:
				case STONE_BUTTON:
				case WALL_SIGN:
				case GLASS: return false;
			}
		return true;
	}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityExposedToSky.class, Pattern.compile("(!)?" + ModDamage.entityPart + "exposedtosky", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityExposedToSky getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityExposedToSky(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"));
		return null;
	}

}
