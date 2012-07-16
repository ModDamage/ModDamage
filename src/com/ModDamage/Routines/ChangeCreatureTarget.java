package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class ChangeCreatureTarget extends Routine
{
	private final IDataProvider<Creature> creatureDP;
	private final IDataProvider<LivingEntity> targetDP;
	protected ChangeCreatureTarget(String configString, IDataProvider<Creature> creatureDP, IDataProvider<LivingEntity> targetDP)
	{
		super(configString);
		this.creatureDP = creatureDP;
		this.targetDP = targetDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Creature creature = creatureDP.get(data);
		if (creature == null) return;
		LivingEntity target = null;
		if (targetDP != null) target = targetDP.get(data);

		creature.setTarget(target);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)\\.(?:change|set)?target\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ChangeCreatureTarget getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<Creature> creatureDP = DataProvider.parse(info, Creature.class, matcher.group(1));
			if (creatureDP == null) return null;
			
			IDataProvider<LivingEntity> targetDP = DataProvider.parse(info, LivingEntity.class, matcher.group(2));
			if (targetDP == null && !matcher.group(2).toLowerCase().equals("none")) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Change Target: " + creatureDP + " " + targetDP);
			return new ChangeCreatureTarget(matcher.group(), creatureDP, targetDP);
		}
	}
}
