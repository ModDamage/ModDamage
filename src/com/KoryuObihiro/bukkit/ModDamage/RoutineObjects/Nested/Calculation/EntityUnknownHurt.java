package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntityUnknownHurt extends EntityCalculationRoutine
{
	public EntityUnknownHurt(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, entityReference, match);
	}
	
	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.LIVING))
			((LivingEntity)entityReference.getEntity(eventInfo)).damage(input);
	}

	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.unknownhurt", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public EntityUnknownHurt getNew(Matcher matcher, DynamicInteger match)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new EntityUnknownHurt(matcher.group(), reference, match);
			return null;
		}
	}
}