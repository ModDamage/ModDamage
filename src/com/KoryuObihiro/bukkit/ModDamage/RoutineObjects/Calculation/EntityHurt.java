package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntityHurt extends EntityCalculationRoutine
{
	public EntityHurt(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input)
	{
		Entity targetEntity = entityReference.getEntity(eventInfo);
		if(targetEntity != null && entityReference.getElement(eventInfo).matchesType(ModDamageElement.LIVING))
		{
			LivingEntity entity = ((LivingEntity)targetEntity);//XXX Need to allocate this?
			if(entityReference.getEntityOther(eventInfo) != null)
			{
				int damageValue = value.getValue(eventInfo);
				Bukkit.getPluginManager().callEvent( new EntityDamageByEntityEvent(entityReference.getEntityOther(eventInfo), entity, DamageCause.CUSTOM, damageValue));
				entity.damage(damageValue);
			}
		}
	}

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
