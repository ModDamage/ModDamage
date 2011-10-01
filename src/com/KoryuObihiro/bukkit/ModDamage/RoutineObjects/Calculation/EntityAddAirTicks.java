package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntityAddAirTicks extends LivingEntityCalculationRoutine
{
	public EntityAddAirTicks(String configString, EntityReference entityReference, IntegerMatch match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void applyEffect(LivingEntity entity, int input) 
	{
		entity.setRemainingAir(entity.getRemainingAir() + input);
	}

	public static void register()
	{
		CalculationRoutine.registerCalculation(EntityAddAirTicks.class, Pattern.compile("(\\w+)effect\\.addAirTicks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAddAirTicks getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new EntityAddAirTicks(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}