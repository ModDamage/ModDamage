package com.ModDamage.RoutineObjects.Nested.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntityExplode extends EntityCalculationRoutine
{
	public EntityExplode(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, entityReference, match);
	}
	
	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input) 
	{
		Entity entity = entityReference.getEntity(eventInfo);
		entity.getWorld().createExplosion(entity.getLocation(), (float)input/10);
	}
	
	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(.*)effect\\.explode", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public EntityExplode getNew(Matcher matcher, DynamicInteger match)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new EntityExplode(matcher.group(), reference, match);
			return null;
		}
	}
}
