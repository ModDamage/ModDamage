package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntityHurt extends LivingEntityCalculationRoutine
{
	public EntityHurt(String configString, EntityReference entityReference, IntegerMatch match)
	{
		super(configString, entityReference, match);
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		Entity targetEntity = entityReference.getEntity(eventInfo);
		if(entityReference.getEntity(eventInfo) != null && entityReference.getEntity(eventInfo) instanceof LivingEntity)
		{
			if(entityReference.getEntityOther(eventInfo) != null)
				((LivingEntity)targetEntity).damage(value.getValue(eventInfo), entityReference.getEntityOther(eventInfo));
			else ((LivingEntity)targetEntity).damage(value.getValue(eventInfo));
		}
	}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input){}

	public static void register()
	{
		CalculationRoutine.registerCalculation(EntityHurt.class, Pattern.compile("(\\w+)effect\\.hurt", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHurt getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new EntityHurt(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}
