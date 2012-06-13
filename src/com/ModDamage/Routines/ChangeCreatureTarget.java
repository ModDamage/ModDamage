package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class ChangeCreatureTarget extends Routine
{
	private final DataRef<Creature> creatureRef;
	private final DataRef<LivingEntity> targetRef;
	protected ChangeCreatureTarget(String configString, DataRef<Creature> creatureRef, DataRef<LivingEntity> targetRef)
	{
		super(configString);
		this.creatureRef = creatureRef;
		this.targetRef = targetRef;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Creature creature = creatureRef.get(data);
		if (creature == null) return;
		LivingEntity target = null;
		if (targetRef != null) target = targetRef.get(data);

		creature.setTarget(target);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+).(?:change|set)?target.([a-z]+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ChangeCreatureTarget getNew(Matcher matcher, EventInfo info)
		{ 
			DataRef<Creature> creatureRef = info.get(Creature.class, matcher.group(1).toLowerCase());
			if (creatureRef == null) return null;
			
			DataRef<LivingEntity> targetRef = info.get(LivingEntity.class, matcher.group(2).toLowerCase());
			if (targetRef == null && !matcher.group(2).toLowerCase().equals("none")) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Change Target: " + creatureRef + " " + targetRef);
			return new ChangeCreatureTarget(matcher.group(), creatureRef, targetRef);
		}
	}
}
