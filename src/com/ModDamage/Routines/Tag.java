package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class Tag extends NestedRoutine
{
	private final String tag;
	private final DataRef<Entity> entityRef;
	private final DataRef<World> worldRef;
	private final IntegerExp integer;
	
	protected Tag(String configString, String tag, DataRef<Entity> entityRef, DataRef<World> worldRef, IntegerExp integer)
	{
		super(configString);
		this.tag = tag;
		this.entityRef = entityRef;
		this.worldRef = worldRef;
		this.integer = integer;
	}
	
	protected Tag(String configString, String tag, DataRef<Entity> entityRef, DataRef<World> worldRef)
	{
		super(configString);
		this.tag = tag;
		this.entityRef = entityRef;
		this.worldRef = worldRef;
		this.integer = null;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		if (worldRef != null)
		{
			World world = worldRef.get(data);
			if (world != null)
			{
				if(integer != null)
				{
					Integer oldTagValue = ModDamage.getTagger().getTagValue(world, tag);
					EventData myData = myInfo.makeChainedData(data, new IntRef(oldTagValue == null? 0 : oldTagValue));
					ModDamage.getTagger().addTag(world, tag, integer.getValue(myData));
				}
				else
					ModDamage.getTagger().removeTag(world, tag);
			}
		}
		else
		{
			Entity entity = entityRef.get(data);
			if(entity != null)
			{
				if(integer != null)
				{
					Integer oldTagValue = ModDamage.getTagger().getTagValue(entity, tag);
					EventData myData = myInfo.makeChainedData(data, new IntRef(oldTagValue == null? 0 : oldTagValue));
					ModDamage.getTagger().addTag(entity, tag, integer.getValue(myData));
				}
				else
					ModDamage.getTagger().removeTag(entity, tag);
			}
		}
	}

	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("untag\\.(\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("tag\\.(\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	public static final EventInfo myInfo = new SimpleEventInfo(IntRef.class, "value", "-default");
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<Entity> entityRef = null;
			DataRef<World> worldRef = null;
			
			if (name.equals("world"))
			{
				worldRef = info.get(World.class, name);
				if(worldRef == null) return null;
			}
			else
			{
				entityRef = info.get(Entity.class, name);
				if(entityRef == null) return null;
			}
			
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Untag: " + matcher.group(1) + ", " + matcher.group(2));
			return new Tag(matcher.group(), matcher.group(2).toLowerCase(), entityRef, worldRef);
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			DataRef<Entity> entityRef = null;
			DataRef<World> worldRef = null;
			
			if (name.equals("world"))
			{
				worldRef = info.get(World.class, name);
				if(worldRef == null) return null;
			}
			else
			{
				entityRef = info.get(Entity.class, name);
				if(entityRef == null) return null;
			}
			
			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines == null) return null;
			IntegerExp integer = IntegerExp.getNew(routines, einfo);
			if(integer == null) return null;
			
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Tag: " + matcher.group(1) + ", " + matcher.group(2) + ", " + integer.toString());
			return new Tag(matcher.group(), matcher.group(2).toLowerCase(), entityRef, worldRef, integer);
		}
	}
}
