package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.Backend.ModDamageElement;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;

public class EntitySpawn extends NestedRoutine
{
	private final DataRef<Entity> entityRef;
	private final ModDamageElement spawnElement;
	private final Routines routines;
	public EntitySpawn(String configString, DataRef<Entity> entityRef, ModDamageElement spawnElement, Routines routines)
	{
		super(configString);
		this.entityRef = entityRef;
		this.spawnElement = spawnElement;
		this.routines = routines;
	}
	
	static EventInfo myInfo = new SimpleEventInfo(
			Entity.class, "spawned",
			ModDamageElement.class, "spawned",
			Integer.class, "health", "-default");

	@Override
	public void run(EventData data) 
	{
		Entity entity = entityRef.get(data);
		if (entity == null)
			return; //entity = EntityReference.TARGET.getEntity(data);
		
		LivingEntity newEntity = spawnElement.spawnCreature(entity.getLocation());
		
		EventData newData = myInfo.makeChainedData(data, 
				newEntity, ModDamageElement.getElementFor(newEntity), newEntity.getHealth());
		
		routines.run(newData);
		
		newEntity.setHealth(newData.getMy(Integer.class, 2));
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.spawn\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public EntitySpawn getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			ModDamageElement element = ModDamageElement.getElementNamed(matcher.group(2));
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(element != null && element.canSpawnCreature() && entityRef != null && routines != null)
				return new EntitySpawn(matcher.group(), entityRef, element, routines);
			return null;
		}
	}
}
