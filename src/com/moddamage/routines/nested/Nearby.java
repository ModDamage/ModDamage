package com.moddamage.routines.nested;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.moddamage.LogUtil;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.eventinfo.SimpleEventInfo;
import com.moddamage.matchables.EntityType;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class Nearby extends NestedRoutine
{
	private final boolean nearest;
	private final IDataProvider<Entity> entityDP;
	private final EntityType filterElement;
	private final IDataProvider<Integer> radius;

	protected Nearby(ScriptLine scriptLine, boolean nearest, IDataProvider<Entity> entityDP, EntityType filterElement, IDataProvider<Integer> radius)
	{
		super(scriptLine);
		this.nearest = nearest;
		this.entityDP = entityDP;
		this.filterElement = filterElement;
		this.radius = radius;
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
		NestedRoutine.registerRoutine(Pattern.compile("near(by|est)\\.([^.]*)\\.([^.]*)\\.([^.]*)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			boolean nearest = matcher.group(1).equalsIgnoreCase("est");
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(2)); if (entityDP == null) return null;
			EntityType element = EntityType.getElementNamed(matcher.group(3)); if (element == null) return null;
			IDataProvider<Integer> radius = DataProvider.parse(info, Integer.class, matcher.group(4)); if (radius == null) return null;

			LogUtil.info("Near" + (nearest? "est" : "by") + ": " + entityDP + ", " + element + ", " + radius);

			EventInfo einfo = info.chain(myInfo);
			
			Nearby routine = new Nearby(scriptLine, nearest, entityDP, element, radius);
			return new NestedRoutineBuilder(routine, routine.routines, einfo);
		}
	}
}
