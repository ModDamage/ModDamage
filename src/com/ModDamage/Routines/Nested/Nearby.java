package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Routines;

public class Nearby extends NestedRoutine
{
	private final IDataProvider<Entity> entityDP;
	private final EntityType filterElement;
	private final IDataProvider<Integer> radius;
	private final Routines routines;

	protected Nearby(String configString, IDataProvider<Entity> entityDP, EntityType filterElement, IDataProvider<Integer> radius, Routines routines)
	{
		super(configString);
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
		int r = radius.get(data);
		ENTITIES: for (Entity e : entity.getNearbyEntities(r, r, r))
		{
			if (entClass.isAssignableFrom(e.getClass())){
				EventData newData = myInfo.makeChainedData(data, e);
				
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
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1)); if (entityDP == null) return null;
			EntityType element = EntityType.getElementNamed(matcher.group(2)); if (element == null) return null;
			IDataProvider<Integer> radius = DataProvider.parse(info, Integer.class, matcher.group(3)); if (radius == null) return null;

			ModDamage.addToLogRecord(OutputPreset.INFO, "Nearby: " + entityDP + ", " + element + ", " + radius);
			
			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines != null)
				return new Nearby(matcher.group(), entityDP, element, radius, routines);
			return null;
		}
	}
}
