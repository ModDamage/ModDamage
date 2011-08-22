package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySpawn extends EntityCalculatedEffectRoutine
{
	final CreatureType creatureType;
	public EntitySpawn(boolean forAttacker, CreatureType creatureType, List<Routine> routines)
	{ 
		super(forAttacker, routines);
		this.creatureType = creatureType;
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		if(input > 0)
			for(int i = 0; i < input; i++)
				affectedObject.getLocation().getWorld().spawnCreature(affectedObject.getLocation(), creatureType);
	}
	
	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntitySpawn.class, Pattern.compile("(\\w+)effect\\.spawn\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySpawn getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
		{
			ModDamageElement element = ModDamageElement.matchElement(matcher.group(2));
			CreatureType creatureType = (element != null)?element.getCreatureType():null;
			return new EntitySpawn((ModDamage.matchesValidEntity(matcher.group(1)))?ModDamage.matchEntity(matcher.group(1)):false, creatureType, routines);
		}
		return null;
	}
}
