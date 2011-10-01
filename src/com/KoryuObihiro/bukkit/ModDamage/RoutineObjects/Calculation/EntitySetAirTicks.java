package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntitySetAirTicks extends LivingEntityCalculationRoutine
{
	public EntitySetAirTicks(String configString, EntityReference entityReference, IntegerMatch match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(input);
	}
	
	public static void register()
	{
		CalculationRoutine.registerCalculation(EntitySetAirTicks.class, Pattern.compile("(\\w+)effect\\.setairticks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySetAirTicks getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new EntitySetAirTicks(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}
