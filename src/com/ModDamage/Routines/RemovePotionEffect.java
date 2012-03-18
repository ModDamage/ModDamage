package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class RemovePotionEffect extends Routine
{
	private final DataRef<LivingEntity> entityRef;
	private final PotionEffectType type;
	
	protected RemovePotionEffect(String configString, DataRef<LivingEntity> entityRef, PotionEffectType type)
	{
		super(configString);
		this.entityRef = entityRef;
		this.type = type;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		LivingEntity entity = entityRef.get(data);
		if (entity == null) return;

		entity.removePotionEffect(type);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+?)(?:effect)?\\.removepotioneffect\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public RemovePotionEffect getNew(Matcher matcher, EventInfo info)
		{ 
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, matcher.group(1).toLowerCase());
			if (entityRef == null) return null;
			
			PotionEffectType type = PotionEffectType.getByName(matcher.group(2).toUpperCase());
			if (type == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown potion effect type '"+matcher.group(2)+"'");
				return null;
			}
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "RemovePotionEffect: from " + entityRef + ", " + type);
			return new RemovePotionEffect(matcher.group(), entityRef, type);
		}
	}
}
