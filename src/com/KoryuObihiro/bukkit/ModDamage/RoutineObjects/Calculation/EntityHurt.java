package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntityHurt extends LivingEntityCalculationRoutine
{
	public EntityHurt(String configString, EntityReference entityReference, List<Routine> routines)
	{
		super(configString, entityReference, routines);
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		Entity targetEntity = entityReference.getEntity(eventInfo);
		if(entityReference.getEntity(eventInfo) != null && entityReference.getEntity(eventInfo) instanceof LivingEntity)
		{
			if(entityReference.getEntityOther(eventInfo) != null)
				((LivingEntity)targetEntity).damage(calculateInputValue(eventInfo), entityReference.getEntityOther(eventInfo));
			else ((LivingEntity)targetEntity).damage(calculateInputValue(eventInfo));
		}
	}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input){}

	public static void register()
	{
		CalculationRoutine.register(EntityHurt.class, Pattern.compile("(\\w+)effect\\.hurt", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHurt getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null && EntityReference.isValid(matcher.group(1)))
			return new EntityHurt(matcher.group(), EntityReference.match(matcher.group(1)), routines);
		return null;
	}
}
