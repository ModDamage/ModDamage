package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.Backend.EntityType;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

public class Nearby extends NestedRoutine
{
	private final DataRef<Entity> entityRef;
	private final EntityType filterElement;
	private final DynamicInteger radius;
	private final Routines routines;

	protected Nearby(String configString, DataRef<Entity> entityRef, EntityType filterElement, DynamicInteger radius, Routines routines)
	{
		super(configString);
		this.entityRef = entityRef;
		this.filterElement = filterElement;
		this.radius = radius;
		this.routines = routines;
	}

	static EventInfo myInfo = new SimpleEventInfo(
			Entity.class, EntityType.class, "nearby", "it");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Class<?> entClass = filterElement.myClass;
		LivingEntity entity = (LivingEntity) entityRef.get(data);
		int r = radius.getValue(data);
		ENTITIES: for (Entity e : entity.getNearbyEntities(r, r, r))
		{
			if (entClass.isAssignableFrom(e.getClass())){
				EventData newData = myInfo.makeChainedData(data, e, EntityType.get(e));
				
				routines.run(newData);
				
				continue ENTITIES;
			}
		}
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("nearby\\.([^.]*)\\.([^.]*)\\.([^.]*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Nearby getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			EntityType element = EntityType.getElementNamed(matcher.group(2));
			DynamicInteger radius = DynamicInteger.getNew(matcher.group(3), info);

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(element != null && entityRef != null && radius != null && routines != null)
				return new Nearby(matcher.group(), entityRef, element, radius, routines);
			return null;
		}
	}
}
