package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.ModDamage.LogUtil;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Matchables.EntityType;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class Spawn extends NestedRoutine
{
	private final IDataProvider<Location> locDP;
	private final EntityType spawnType;
	public Spawn(ScriptLine scriptLine, IDataProvider<Location> locDP, EntityType spawnType)
	{
		super(scriptLine);
		this.locDP = locDP;
		this.spawnType = spawnType;
	}
	
	static EventInfo myInfo = new SimpleEventInfo(
			Entity.class, "spawned",
			Double.class, "health", "-default");

	@Override
	public void run(EventData data) throws BailException 
	{
		Location loc = locDP.get(data);
		if (loc == null)
			return; //entity = EntityReference.TARGET.getEntity(data);
		
		LivingEntity newEntity = spawnType.spawn(loc);
		
		EventData newData = myInfo.makeChainedData(data, 
				newEntity, newEntity.getHealth());
		
		routines.run(newData);
		
		newEntity.setHealth(newData.get(Double.class, newData.start + 1));
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*)effect\\.spawn\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}
	
	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			EntityType spawnType = EntityType.getElementNamed(matcher.group(2));
			if (spawnType == null) return null;
			if (!spawnType.canSpawn())
			{
				LogUtil.error("Cannot spawn "+matcher.group(2));
				return null;
			}
			IDataProvider<Location> locDP = DataProvider.parse(info, Location.class, matcher.group(1));
            if (locDP == null) return null;

            LogUtil.info("Spawn "+spawnType+" at "+locDP);

			EventInfo einfo = info.chain(myInfo);

			Spawn routine = new Spawn(scriptLine, locDP, spawnType);
            return new NestedRoutineBuilder(routine, routine.routines, einfo);
		}
	}
}
