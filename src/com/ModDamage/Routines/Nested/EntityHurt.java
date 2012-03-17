package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ModDamage.ModDamage;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routines;

public class EntityHurt extends NestedRoutine
{
	private final DataRef<LivingEntity> entityRef;
	private final DataRef<Entity> entityOtherRef;
	private final IntegerExp hurt_amount;
	
	public EntityHurt(String configString, DataRef<LivingEntity> entityRef, DataRef<Entity> entityOtherRef, IntegerExp hurt_amount)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityOtherRef = entityOtherRef;
		this.hurt_amount = hurt_amount;
	}

	static final EventInfo myInfo = new SimpleEventInfo(IntRef.class, "hurt_amount", "-default");
	
	@Override
	public void run(EventData data) throws BailException
	{
		final LivingEntity target = (LivingEntity) entityRef.get(data);
		final Entity from = entityOtherRef.get(data);
		if(from != null && target != null && target.getHealth() > 0 && !target.isDead())
		{
			final EventData myData = myInfo.makeChainedData(data, new IntRef(0));
			final int damage = hurt_amount.getValue(myData);
			Bukkit.getScheduler().scheduleAsyncDelayedTask(ModDamage.getPluginConfiguration().plugin, new Runnable()
				{
					@Override
					public void run()
					{
						EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(from, target, DamageCause.ENTITY_ATTACK, damage);
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled())
							target.damage(event.getDamage());
					}
				});
		}
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.hurt", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public EntityHurt getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, name);
			String otherName = "-" + name + "-other";
			DataRef<Entity> entityOtherRef = info.get(Entity.class, otherName);

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IntegerExp hurt_amount = IntegerExp.getNew(routines, einfo);
			
			if(entityRef != null && entityOtherRef != null)
				return new EntityHurt(matcher.group(), entityRef, entityOtherRef, hurt_amount);
			return null;
		}
	}
}
