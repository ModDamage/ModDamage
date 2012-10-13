package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Despawn extends Routine
{
	private final IDataProvider<Entity> entityDP;
	protected Despawn(String configString, IDataProvider<Entity> entityDP)
	{
		super(configString);
		this.entityDP = entityDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;

		entity.remove();
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)\\.(?:despawn|remove)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Despawn getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Despawn: " + entityDP);
			return new Despawn(matcher.group(), entityDP);
		}
	}
}
