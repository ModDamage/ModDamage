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
	private final IDataProvider<Integer> x, y, z, yaw, pitch;
	protected Teleport(String configString, IDataProvider<Entity> entityDP, IDataProvider<Integer> x, IDataProvider<Integer> y, IDataProvider<Integer> z, IDataProvider<Integer> yaw, IDataProvider<Integer> pitch)
	{
		super(configString);
		this.entityDP = entityDP;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;

		Location loc;
		if (yaw != null && pitch != null)
			loc = new Location(entity.getWorld(), x.get(data), y.get(data), z.get(data), yaw.get(data), pitch.get(data));
		else
			loc = new Location(entity.getWorld(), x.get(data), y.get(data), z.get(data), entity.getLocation().getYaw(), entity.getLocation().getPitch());
		entity.teleport(loc);
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
			
			IDataProvider<Integer> x, y, z;
			x = DataProvider.parse(info, Integer.class, sm.spawn()); if (x == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			y = DataProvider.parse(info, Integer.class, sm.spawn()); if (y == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			z = DataProvider.parse(info, Integer.class, sm.spawn()); if (z == null) return null;
			
			IDataProvider<Integer> yaw = null, pitch = null;
			String yaw_pitch = "";
			if (!sm.isEmpty()) {
				if (!sm.matchesFront(dotPattern)) return null;
				yaw = DataProvider.parse(info, Integer.class, sm.spawn()); if (yaw == null) return null;
				if (!sm.matchesFront(dotPattern)) return null;
				pitch = DataProvider.parse(info, Integer.class, sm.spawn()); if (pitch == null) return null;
				
				if (!sm.isEmpty()) return null;
				
				yaw_pitch = ", "+yaw+", "+pitch;
			}
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Teleport: " + entityDP + " to " + x + ", " + y + ", " + z + yaw_pitch);
			return new Teleport(matcher.group(), entityDP, x, y, z, yaw, pitch);
		}
	}
}
