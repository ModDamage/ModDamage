package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class TeleportRoutine extends Routine
{
	private final DataRef<Entity> entityRef;
	private final DynamicInteger x, y, z;
	protected TeleportRoutine(String configString, DataRef<Entity> entityRef, DynamicInteger x, DynamicInteger y, DynamicInteger z)
	{
		super(configString);
		this.entityRef = entityRef;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityRef.get(data);
		if (entity == null) return;

		entity.teleport(new Location(entity.getWorld(), x.getValue(data), y.getValue(data), z.getValue(data)));
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+?)(?:effect)?.teleport.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	private static Pattern dotPattern = Pattern.compile("\\s*\\.\\s*");

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public TeleportRoutine getNew(Matcher matcher, EventInfo info)
		{ 
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			
			StringMatcher sm = new StringMatcher(matcher.group(2));
			DynamicInteger x = DynamicInteger.getIntegerFromFront(sm.spawn(), info); if (x == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			DynamicInteger y = DynamicInteger.getIntegerFromFront(sm.spawn(), info); if (y == null) return null;
			if (!sm.matchesFront(dotPattern)) return null;
			DynamicInteger z = DynamicInteger.getIntegerFromFront(sm.spawn(), info); if (z == null) return null;
			if (!sm.isEmpty()) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Teleport: " + entityRef + " to " + x + ", " + y + ", " + z);
			return new TeleportRoutine(matcher.group(), entityRef, x, y, z);
		}
	}
}
