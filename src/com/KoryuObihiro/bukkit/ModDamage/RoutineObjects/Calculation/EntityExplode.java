package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntityExplode extends EntityCalculationRoutine<Entity>
{
	public EntityExplode(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, entityReference, routines);
	}
	
	@Override
	protected void applyEffect(Entity entity, int input) 
	{
		entity.getWorld().createExplosion(entity.getLocation(), (float)input/10);
	}
	
	public static void register()
	{
		CalculationRoutine.register(EntityExplode.class, Pattern.compile("(\\w+)effect\\.explode", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityExplode getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null && EntityReference.isValid(matcher.group(1)))
			return new EntityExplode(matcher.group(), EntityReference.match(matcher.group(1)), routines);
		return null;
	}
}
