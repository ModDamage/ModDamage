package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Teleport extends Routine
{
	private final IDataProvider<Entity> entityDP;
	private final IDataProvider<Integer> x, y, z;
	protected Teleport(String configString, IDataProvider<Entity> entityDP, IDataProvider<Integer> x, IDataProvider<Integer> y, IDataProvider<Integer> z)
	{
		super(configString);
		this.entityDP = entityDP;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;

		entity.teleport(new Location(entity.getWorld(), x.get(data), y.get(data), z.get(data)));
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+?)(?:effect)?\\.teleport\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	private static Pattern dotPattern = Pattern.compile("\\s*\\.\\s*");

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Teleport getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			StringMatcher sm = new StringMatcher(matcher.group(2));
			IDataProvider<Integer> x = DataProvider.parse(info, Integer.class, sm.spawn()); if (x == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			IDataProvider<Integer> y = DataProvider.parse(info, Integer.class, sm.spawn()); if (y == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			IDataProvider<Integer> z = DataProvider.parse(info, Integer.class, sm.spawn()); if (z == null) return null;
			if (!sm.isEmpty()) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Teleport: " + entityDP + " to " + x + ", " + y + ", " + z);
			return new Teleport(matcher.group(), entityDP, x, y, z);
		}
	}
}
