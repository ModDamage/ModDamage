package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntitySpawn extends NestedRoutine
{
	final EntityReference entityReference;
	final ModDamageElement spawnElement;
	final Collection<Routine> routines;
	public EntitySpawn(String configString, EntityReference entityReference, ModDamageElement spawnElement, Collection<Routine> routines)
	{
		super(configString);
		this.entityReference = entityReference;
		this.spawnElement = spawnElement;
		this.routines = routines;
	}

	@Override
	public void run(TargetEventInfo eventInfo) 
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if (entity == null)
			return; //entity = EntityReference.TARGET.getEntity(eventInfo);
		
		LivingEntity newEntity = spawnElement.spawnCreature(entity.getLocation());
		
		TargetEventInfo newEventInfo = new TargetEventInfo(newEntity, ModDamageElement.getElementFor(newEntity), newEntity.getHealth());
		
		for (Routine routine : routines)
			routine.run(newEventInfo);
		
		newEntity.setHealth(newEventInfo.eventValue);
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.spawn\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public EntitySpawn getNew(Matcher matcher, Object nestedContent)
		{
			ModDamageElement element = ModDamageElement.getElementNamed(matcher.group(2));
			EntityReference reference = EntityReference.match(matcher.group(1));
			List<Routine> routines = new ArrayList<Routine>();
			if (RoutineAliaser.parseRoutines(routines, nestedContent))
				if(element != null && element.canSpawnCreature() && reference != null)
					return new EntitySpawn(matcher.group(), reference, element, routines);
			return null;
		}
	}
}
