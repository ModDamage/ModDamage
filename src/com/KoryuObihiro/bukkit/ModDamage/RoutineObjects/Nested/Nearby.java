package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Nearby extends NestedRoutine
{
	final EntityReference entityReference;
	final ModDamageElement filterElement;
	final DynamicInteger radius;
	final Collection<Routine> routines;

	protected Nearby(String configString, EntityReference entityReference, ModDamageElement filterElement, DynamicInteger radius, Collection<Routine> routines)
	{
		super(configString);
		this.entityReference = entityReference;
		this.filterElement = filterElement;
		this.radius = radius;
		this.routines = routines;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		Class<?>[] entClasses = filterElement.myClasses;
		LivingEntity entity = (LivingEntity) entityReference.getEntity(eventInfo);
		ModDamageElement entityElement = ModDamageElement.getElementFor(entity);
		int r = radius.getValue(eventInfo);
		ENTITIES: for (Entity e : entity.getNearbyEntities(r, r, r))
		{
			Class<?> eClass = e.getClass();
			for (Class<?> entClass : entClasses)
			{
				if (entClass.isAssignableFrom(eClass)){
					// TODO: change entity references in the EventInfo class to something that makes more sense
					AttackerEventInfo newEventInfo = new AttackerEventInfo((LivingEntity) e, ModDamageElement.getElementFor(e), entity, entityElement, null, ModDamageElement.UNKNOWN, 0);
					
					for (Routine routine : routines)
					{
						routine.run(newEventInfo);
					}
					
					continue ENTITIES;
				}
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
		public Nearby getNew(Matcher matcher, Object nestedContent)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			ModDamageElement element = ModDamageElement.getElementNamed(matcher.group(2));
			DynamicInteger radius = DynamicInteger.getNew(matcher.group(3));
			List<Routine> routines = new ArrayList<Routine>();
			if (RoutineAliaser.parseRoutines(routines, nestedContent))
				if(element != null && element.canSpawnCreature() && reference != null)
					return new Nearby(matcher.group(), reference, element, radius, routines);
			return null;
		}
	}
}
