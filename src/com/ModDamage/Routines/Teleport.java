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
    private final IDataProvider<Location> locDP;
	private final IDataProvider<Integer> yawDP, pitchDP;
	protected Teleport(String configString, IDataProvider<Entity> entityDP, IDataProvider<Location> locDP, IDataProvider<Integer> yawDP, IDataProvider<Integer> pitchDP)
	{
		super(configString);
		this.entityDP = entityDP;
		this.locDP = locDP;
		this.yawDP = yawDP;
		this.pitchDP = pitchDP;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;

		Location loc = locDP.get(data);
        if (loc == null) return;
        float yaw, pitch;
		if (yawDP != null && pitchDP != null) {
			yaw = yawDP.get(data);
            pitch = pitchDP.get(data);
        }
        else {
            yaw = entity.getLocation().getYaw();
            pitch = entity.getLocation().getPitch();
        }

        loc.setYaw(yaw);
        loc.setPitch(pitch);

		entity.teleport(loc);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)(?:effect)?\\.teleport\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
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
			
			IDataProvider<Location> locDP;
            locDP = DataProvider.parse(info, Location.class, sm.spawn()); if (locDP == null) return null;
			
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
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Teleport: " + entityDP + " to " + locDP + yaw_pitch);
			return new Teleport(matcher.group(), entityDP, locDP, yaw, pitch);
		}
	}
}
