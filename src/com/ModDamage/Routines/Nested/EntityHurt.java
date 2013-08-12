package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.NumberExp;
import com.ModDamage.Magic.MagicStuff;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.Routines.Routines;

public class EntityHurt extends NestedRoutine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final IDataProvider<Entity> entityOtherDP;
	private final IDataProvider<Number> hurt_amount;

	public EntityHurt(String configString, IDataProvider<LivingEntity> livingDP, IDataProvider<Entity> entityOtherDP, IDataProvider<Number> hurt_amount)
	{
		super(configString);
		this.livingDP = livingDP;
		this.entityOtherDP = entityOtherDP;
		this.hurt_amount = hurt_amount;
	}

	static final EventInfo myInfo = new SimpleEventInfo(Number.class, "hurt_amount", "-default");

	@Override
	public void run(EventData data) throws BailException
	{
		final LivingEntity target = (LivingEntity) livingDP.get(data);
		final Entity from = entityOtherDP.get(data);
		if(from != null && target != null && target.getHealth() > 0 && !target.isDead())
		{
			final EventData myData = myInfo.makeChainedData(data, 0);
			
			final Number damage = hurt_amount.get(myData);
			if (damage == null) return;
			
			Bukkit.getScheduler().runTask(ModDamage.getPluginConfiguration().plugin, new Runnable()
				{
					@Override
					public void run()
					{
						EntityDamageByEntityEvent event = MagicStuff.craftEntityHurtEvent(from, target, DamageCause.ENTITY_ATTACK, damage);
						if (event == null)
							return;
						
						Bukkit.getPluginManager().callEvent(event);
						if (!event.isCancelled())
							MagicStuff.damageEntity(target, getDamage(event), from);
					}
				});
		}
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.+?)(?:effect)?\\.hurt(?:\\.from\\.(.+))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public EntityHurt getNew(Matcher matcher, Object nestedContent, EventInfo info)
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
                    ModDamage.addToLogRecord(OutputPreset.FAILURE, "The entity '"+livingDP+"' doesn't have a natural opposite. Use .hurt.from.{entity}");
                    return null;
                }
            }

			ModDamage.addToLogRecord(OutputPreset.INFO, "Hurt "+livingDP+":");

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IDataProvider<Number> hurt_amount = NumberExp.getNew(routines, einfo);
			if (hurt_amount == null) return null;

			return new EntityHurt(matcher.group(), livingDP, entityOtherDP, hurt_amount);
		}
	}
	
	////Helper Methods
	private final static Number getDamage(EntityDamageEvent event)
	{
		return MagicStuff.getEventValue(event);
	}
}
