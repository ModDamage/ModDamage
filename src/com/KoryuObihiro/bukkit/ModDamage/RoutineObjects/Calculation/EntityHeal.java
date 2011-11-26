package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

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
		CalculationRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.heal", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public EntityHeal getNew(Matcher matcher, DynamicInteger match)
		{
			if(EntityReference.isValid(matcher.group(1)))
				return new EntityHeal(matcher.group(), EntityReference.match(matcher.group(1)), match);
			return null;
		}
	}
}
