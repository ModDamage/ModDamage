package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityAddAirTicks extends LivingEntityCalculationRoutine
{
	public EntityAddAirTicks(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, entityReference, routines);
	}

	@Override
	protected void applyEffect(LivingEntity entity, int input) 
	{
		entity.setRemainingAir(entity.getRemainingAir() + input);
	}

	public static void register()
	{
		CalculationRoutine.registerStatement(EntityAddAirTicks.class, Pattern.compile("(\\w+)effect\\.addAirTicks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAddAirTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null && EntityReference.isValid(matcher.group(1)))
			return new EntityAddAirTicks(matcher.group(), EntityReference.match(matcher.group(1)), routines);
		return null;
	}

}
