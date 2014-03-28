package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ModDamage.LogUtil;
import com.ModDamage.ModDamage;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class EntityHurt extends Routine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final IDataProvider<Entity> entityOtherDP;
	private final IDataProvider<Number> hurt_amount;

	public EntityHurt(ScriptLine scriptLine, IDataProvider<LivingEntity> livingDP, IDataProvider<Entity> entityOtherDP, IDataProvider<Number> hurt_amount)
	{
		super(scriptLine);
		this.livingDP = livingDP;
		this.entityOtherDP = entityOtherDP;
		this.hurt_amount = hurt_amount;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		final LivingEntity target = (LivingEntity) livingDP.get(data);
		final Entity from = entityOtherDP.get(data);
		if(from != null && target != null && target.getHealth() > 0 && !target.isDead())
		{
			Number ha = hurt_amount.get(data);
			if (ha == null) return;
			
			final double damage = ha.doubleValue();
			Bukkit.getScheduler().runTask(ModDamage.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(from, target, DamageCause.ENTITY_ATTACK, damage);
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled())
							target.damage(event.getDamage(), from);
					}
				});
		}
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)(?:effect)?\\.hurt(?:\\.from\\.(.+))?(?::|\\s+by\\s)\\s*(.+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, name);
			if (livingDP == null) return null;


            IDataProvider<Entity> entityOtherDP;
            if (matcher.group(2) != null)
            {
                entityOtherDP = DataProvider.parse(info, Entity.class, matcher.group(2));

                if (entityOtherDP == null) return null;
            }
            else
            {
                String otherName = "-" + name + "-other";
                entityOtherDP = info.get(Entity.class, otherName);
                if (entityOtherDP == null)
                {
                	LogUtil.error("The entity '"+livingDP+"' doesn't have a natural opposite. Use .hurt.from.{entity}");
                    return null;
                }
            }


			IDataProvider<Number> hurt_amount = DataProvider.parse(info, Number.class, matcher.group(3));
			if (hurt_amount == null) return null;
			
			
			LogUtil.info("Hurt "+livingDP + (matcher.group(2) != null? " from " + entityOtherDP : "") + " by " + hurt_amount);

			return new RoutineBuilder(new EntityHurt(scriptLine, livingDP, entityOtherDP, hurt_amount));
		}
	}
}
