package com.ModDamage.Routines.Nested.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Nested.CalculationRoutine;

public class EntityHeal extends EntityCalculationRoutine
{
	public EntityHeal(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.LIVING))
		{
			LivingEntity entity = (LivingEntity)entityReference.getEntity(eventInfo);
			entity.setHealth(entity.getHealth() + input > entity.getMaxHealth()?entity.getMaxHealth():entity.getHealth() + input);
		}
	}

	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(.*)effect\\.heal", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public EntityHeal getNew(Matcher matcher, DynamicInteger match)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new EntityHeal(matcher.group(), reference, match);
			return null;
		}
	}
}
