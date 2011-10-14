package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntityHurt extends LivingEntityCalculationRoutine
{
	public EntityHurt(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, entityReference, match);
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		Entity targetEntity = entityReference.getEntity(eventInfo);
		if(entityReference.getEntity(eventInfo) != null && entityReference.getEntity(eventInfo) instanceof LivingEntity)
		{
			LivingEntity entity = ((LivingEntity)targetEntity);//XXX Need to allocate this?
			if(entityReference.getEntityOther(eventInfo) != null)
			{
				int damageValue = value.getValue(eventInfo);
				TargetEventInfo.server.getPluginManager().callEvent( new EntityDamageByEntityEvent(entityReference.getEntityOther(eventInfo), entity, DamageCause.CUSTOM, damageValue));
				entity.damage(damageValue);
			}
		}
	}
	
	@Override
	protected void applyEffect(LivingEntity affectedObject, int input){}

	public static void register()
	{
		CalculationRoutine.registerCalculation(EntityHurt.class, Pattern.compile("(\\w+)effect\\.hurt", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHurt getNew(Matcher matcher, DynamicInteger match)
	{
		if(matcher != null && match != null && EntityReference.isValid(matcher.group(1)))
			return new EntityHurt(matcher.group(), EntityReference.match(matcher.group(1)), match);
		return null;
	}
}
