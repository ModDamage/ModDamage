package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Tameable;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class ChangeTameableOwner extends Routine
{
	private final IDataProvider<Creature> creatureDP;
	private final IDataProvider<HumanEntity> ownerDP;
	protected ChangeTameableOwner(String configString, IDataProvider<Creature> tameableDP, IDataProvider<HumanEntity> ownerDP)
	{
		super(configString);
		this.creatureDP = tameableDP;
		this.ownerDP = ownerDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Creature creature = creatureDP.get(data);
		if (creature == null || !(creature instanceof Tameable)) return;
		
		HumanEntity owner = null;
		if (ownerDP != null) owner = ownerDP.get(data);

		((Tameable) creature).setOwner(owner);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)\\.(?:change|set)?owner\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ChangeTameableOwner getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<Creature> creatureDP = DataProvider.parse(info, Creature.class, matcher.group(1));
			if (creatureDP == null) return null;
			
			IDataProvider<HumanEntity> ownerDP = DataProvider.parse(info, HumanEntity.class, matcher.group(2));
			if (ownerDP == null && !matcher.group(2).toLowerCase().equals("none")) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Change Owner: " + creatureDP + " " + ownerDP);
			return new ChangeTameableOwner(matcher.group(), creatureDP, ownerDP);
		}
	}
}
