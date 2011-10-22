package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

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
		CalculationRoutine.registerCalculation(EntityExplode.class, Pattern.compile("(\\w+)effect\\.explode", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityExplode getNew(Matcher matcher, DynamicInteger match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new EntityExplode(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}
