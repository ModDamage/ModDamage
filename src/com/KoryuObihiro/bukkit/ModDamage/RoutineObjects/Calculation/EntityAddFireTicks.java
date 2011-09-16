package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityAddFireTicks extends EntityCalculationRoutine<Entity>
{
	public EntityAddFireTicks(String configString, EntityReference entityReference, List<Routine> routines) 
	{
		super(configString, entityReference, routines);
	}

	@Override
	protected void applyEffect(Entity entity, int input) 
	{
		entity.setFireTicks(entity.getFireTicks() + input);
	}

	public static void register(ModDamage routineUtility)
	{
		CalculationRoutine.registerStatement(EntityAddFireTicks.class, Pattern.compile("(\\w+)effect\\.addFireTicks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAddFireTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null && EntityReference.isValid(matcher.group(1)))
			return new EntityAddFireTicks(matcher.group(), EntityReference.match(matcher.group(1)), routines);
		return null;
	}

}
