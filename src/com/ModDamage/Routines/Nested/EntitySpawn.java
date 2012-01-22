package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Routines.Routines;

public class EntitySpawn extends NestedRoutine
{
	final EntityReference entityReference;
	final ModDamageElement spawnElement;
	final Routines routines;
	public EntitySpawn(String configString, EntityReference entityReference, ModDamageElement spawnElement, Routines routines)
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
		
		routines.run(newEventInfo);
		
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
			Routines routines = RoutineAliaser.parseRoutines(nestedContent);
			if(element != null && element.canSpawnCreature() && reference != null && routines != null)
				return new EntitySpawn(matcher.group(), reference, element, routines);
			return null;
		}
	}
}
