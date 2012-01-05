package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntityHurt extends EntityCalculationRoutine
{
	public EntityHurt(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, entityReference, match);
	}

	@Override
	protected void doCalculation(final TargetEventInfo eventInfo, final int input)
	{
		if(entityReference.getElement(eventInfo).matchesType(ModDamageElement.LIVING))
		{
			final LivingEntity target = (LivingEntity) entityReference.getEntity(eventInfo);
			final Entity from = entityReference.getEntityOther(eventInfo);
			if(from != null && target.getHealth() > 0 && !target.isDead())
			{
				Bukkit.getScheduler().scheduleAsyncDelayedTask(ModDamage.getPluginConfiguration().plugin, new Runnable()
					{
						@Override
						public void run()
						{
							EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(from, target, DamageCause.ENTITY_ATTACK, input);
							Bukkit.getPluginManager().callEvent(event);
							if (!event.isCancelled())
								target.damage(event.getDamage());
						}
					});
			}
		}
	}

	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(.*)effect\\.hurt", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public EntityHurt getNew(Matcher matcher, DynamicInteger match)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(reference != null)
				return new EntityHurt(matcher.group(), reference, match);
			return null;
		}
	}
}
