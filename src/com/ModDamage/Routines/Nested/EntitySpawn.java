package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Routines.Routines;

public class EntitySpawn extends NestedRoutine
{
	private final IDataProvider<Entity> entityDP;
	private final EntityType spawnType;
	private final Routines routines;
	public EntitySpawn(String configString, IDataProvider<Entity> entityDP, EntityType spawnType, Routines routines)
	{
		super(configString);
		this.entityDP = entityDP;
		this.spawnType = spawnType;
		this.routines = routines;
	}
	
	static EventInfo myInfo = new SimpleEventInfo(
			Entity.class, "spawned",
			EntityType.class, "spawned",
			Integer.class, "health", "-default");

	@Override
	public void run(EventData data) throws BailException 
	{
		Entity entity = entityDP.get(data);
		if (entity == null)
			return; //entity = EntityReference.TARGET.getEntity(data);
		
		LivingEntity newEntity = spawnType.spawnCreature(entity.getLocation());
		
		EventData newData = myInfo.makeChainedData(data, 
				newEntity, EntityType.get(newEntity), newEntity.getHealth());
		
		routines.run(newData);
		
		newEntity.setHealth(newData.get(Integer.class, newData.start + 2));
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
			EntityType spawnType = EntityType.getElementNamed(matcher.group(2));
			if (spawnType == null) return null;
			if (!spawnType.canSpawnCreature())
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Cannot spawn "+matcher.group(2));
				return null;
			}
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(entityDP != null && routines != null)
				return new EntitySpawn(matcher.group(), entityDP, spawnType, routines);
			return null;
		}
	}
}
