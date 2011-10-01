package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntityAddFireTicks extends EntityCalculationRoutine<Entity>
{
	public EntityAddFireTicks(String configString, EntityReference entityReference, IntegerMatch match) 
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void applyEffect(Entity entity, int input) 
	{
		entity.setFireTicks(entity.getFireTicks() + input);
	}

	@Override
	protected Entity getAffectedObject(TargetEventInfo eventInfo) { return entityReference.getEntity(eventInfo); }

	public static void register()
	{
		CalculationRoutine.registerCalculation(EntityAddFireTicks.class, Pattern.compile("(\\w+)effect\\.addFireTicks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAddFireTicks getNew(Matcher matcher, IntegerMatch integerMatch)
	{
		if(matcher != null && integerMatch != null)
			if(EntityReference.isValid(matcher.group(1)))
				return new EntityAddFireTicks(matcher.group(), EntityReference.match(matcher.group(1)), integerMatch);
		return null;
	}
}