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
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Routines;

public class EntityHurt extends NestedRoutine
{
	private final IDataProvider<LivingEntity> livingDP;
	private final IDataProvider<Entity> entityOtherDP;
	private final IDataProvider<Integer> hurt_amount;

	public EntityHurt(String configString, IDataProvider<LivingEntity> livingDP, IDataProvider<Entity> entityOtherDP, IDataProvider<Integer> hurt_amount)
	{
		super(configString);
		this.livingDP = livingDP;
		this.entityOtherDP = entityOtherDP;
		this.hurt_amount = hurt_amount;
	}

	static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "hurt_amount", "-default");

	@Override
	public void run(EventData data) throws BailException
	{
		final LivingEntity target = (LivingEntity) livingDP.get(data);
		final Entity from = entityOtherDP.get(data);
		if(from != null && target != null && target.getHealth() > 0 && !target.isDead())
		{
			final EventData myData = myInfo.makeChainedData(data, 0);
			final int damage = hurt_amount.get(myData);
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
			IDataProvider<LivingEntity> livingDP = DataProvider.parse(info, LivingEntity.class, name);
			if (livingDP == null) return null;

			String otherName = "-" + name + "-other";
			IDataProvider<Entity> entityOtherDP = info.get(Entity.class, otherName);
			if (entityOtherDP == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "The entity '"+livingDP+"' doesn't have a natural opposite. Maybe you want unknownhurt instead?");
				return null;
			}

			ModDamage.addToLogRecord(OutputPreset.INFO, "Hurt "+livingDP+":");

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			IDataProvider<Integer> hurt_amount = IntegerExp.getNew(routines, einfo);
			if (hurt_amount == null) return null;

			return new EntityHurt(matcher.group(), livingDP, entityOtherDP, hurt_amount);
		}
	}
}
