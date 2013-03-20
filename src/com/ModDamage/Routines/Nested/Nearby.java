package com.ModDamage.Routines.Nested;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Routines.Routines;

public class Nearby extends NestedRoutine
{
	private final boolean nearest;
	private final IDataProvider<Entity> entityDP;
	private final EntityType filterElement;
	private final IDataProvider<Integer> radius;
	private final Routines routines;

	protected Nearby(String configString, boolean nearest, IDataProvider<Entity> entityDP, EntityType filterElement, IDataProvider<Integer> radius, Routines routines)
	{
		super(configString);
		this.nearest = nearest;
		this.entityDP = entityDP;
		this.filterElement = filterElement;
		this.radius = radius;
		this.routines = routines;
	}

	static EventInfo myInfo = new SimpleEventInfo(
			Entity.class, "nearby", "it");

	@Override
	public void run(EventData data) throws BailException
	{
		Class<?> entClass = filterElement.myClass;
		LivingEntity entity = (LivingEntity) entityDP.get(data);
        if (entity == null) return;

		Integer r = radius.get(data);
		if (r == null) return;
		List<Entity> entities = entity.getNearbyEntities(r, r, r);
		if (entities.isEmpty()) return;

		if (nearest) {
			Location eloc = entity.getLocation();

			double distance = Float.MAX_VALUE;
			Entity nearestEntity = null;

			for (Entity e : entities) {
				if (!entClass.isAssignableFrom(e.getClass())) continue;

				double edist = eloc.distanceSquared(e.getLocation());
				if (edist < distance) {
					distance = edist;
					nearestEntity = e;
				}
			}

			if (nearestEntity != null)
			{
				EventData newData = myInfo.makeChainedData(data, nearestEntity);

				routines.run(newData);
			}
		}
		else
		{
			for (Entity e : entities)
			{
				if (entClass.isAssignableFrom(e.getClass())){
					EventData newData = myInfo.makeChainedData(data, e);

					routines.run(newData);

					continue;
				}
			}
		}
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("near(by|est)\\.([^.]*)\\.([^.]*)\\.([^.]*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Nearby getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			boolean nearest = matcher.group(1).equalsIgnoreCase("est");
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(2)); if (entityDP == null) return null;
			EntityType element = EntityType.getElementNamed(matcher.group(3)); if (element == null) return null;
			IDataProvider<Integer> radius = DataProvider.parse(info, Integer.class, matcher.group(4)); if (radius == null) return null;

			ModDamage.addToLogRecord(OutputPreset.INFO, "Near" + (nearest? "est" : "by") + ": " + entityDP + ", " + element + ", " + radius);

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines != null)
				return new Nearby(matcher.group(), nearest, entityDP, element, radius, routines);
			return null;
		}
	}
}
