package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntitySetFireTicks extends EntityCalculationRoutine<Entity>
{
	public EntitySetFireTicks(String configString, EntityReference entityReference, IntegerMatch match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void applyEffect(Entity affectedObject, int input) 
	{
		affectedObject.setFireTicks(input);
	}

	public static void register()
	{
		CalculationRoutine.registerCalculation(EntitySetFireTicks.class, Pattern.compile("(\\w+)effect\\.setfireticks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySetFireTicks getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new EntitySetFireTicks(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}
