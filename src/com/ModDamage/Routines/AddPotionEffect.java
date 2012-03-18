package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class AddPotionEffect extends Routine
{
	private final DataRef<LivingEntity> entityRef;
	private final PotionEffectType type;
	private final IntegerExp duration, amplifier;
	
	protected AddPotionEffect(String configString, DataRef<LivingEntity> entityRef, PotionEffectType type, IntegerExp duration, IntegerExp amplifier)
	{
		super(configString);
		this.entityRef = entityRef;
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		LivingEntity entity = entityRef.get(data);
		if (entity == null) return;

		entity.addPotionEffect(new PotionEffect(type, duration.getValue(data), amplifier.getValue(data)), true);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+?)(?:effect)?\\.addpotioneffect\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	private static Pattern dotPattern = Pattern.compile("\\s*\\.\\s*");

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public AddPotionEffect getNew(Matcher matcher, EventInfo info)
		{ 
			DataRef<LivingEntity> entityRef = info.get(LivingEntity.class, matcher.group(1).toLowerCase());
			if (entityRef == null) return null; 
			
			PotionEffectType type = PotionEffectType.getByName(matcher.group(2).toUpperCase());
			if (type == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unknown potion effect type '"+matcher.group(2)+"'");
				return null;
			}
			
			StringMatcher sm = new StringMatcher(matcher.group(3));
			IntegerExp duration = IntegerExp.getIntegerFromFront(sm.spawn(), info); if (duration == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			IntegerExp amplifier = IntegerExp.getIntegerFromFront(sm.spawn(), info); if (amplifier == null) return null;
			if (!sm.isEmpty()) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "AddPotionEffect: to " + entityRef + ", " + type + ", " + duration + ", " + amplifier);
			return new AddPotionEffect(matcher.group(), entityRef, type, duration, amplifier);
		}
	}
}
