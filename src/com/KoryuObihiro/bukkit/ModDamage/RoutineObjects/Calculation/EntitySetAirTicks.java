package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntitySetAirTicks extends LivingEntityCalculationRoutine
{
	public EntitySetAirTicks(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, entityReference, routines);
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(input);
	}
	
	public static void register()
	{
		CalculationRoutine.register(EntitySetAirTicks.class, Pattern.compile("(\\w+)effect\\.setairticks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySetAirTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null && EntityReference.isValid(matcher.group(1)))
			return new EntitySetAirTicks(matcher.group(), EntityReference.match(matcher.group(1)), routines);
		return null;
	}
}
