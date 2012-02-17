package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class Tag extends NestedRoutine
{
	protected final String tag;
	protected final DataRef<Entity> entityRef;
	protected final DynamicInteger integer;
	
	protected Tag(String configString, String tag, DataRef<Entity> entityRef, DynamicInteger integer)
	{
		super(configString);
		this.tag = tag;
		this.entityRef = entityRef;
		this.integer = integer;
	}
	
	protected Tag(String configString, String tag, DataRef<Entity> entityRef)
	{
		super(configString);
		this.tag = tag;
		this.entityRef = entityRef;
		this.integer = null;
	}

	@Override
	public void run(EventData data)
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
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			if(entityRef != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "Untag: " + matcher.group(1) + ", " + matcher.group(2));
				return new Tag(matcher.group(), matcher.group(2).toLowerCase(), entityRef);
			}
			return null;
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			if(entityRef != null)
			{
				EventInfo einfo = info.chain(myInfo);
				Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
				if(routines == null) return null;
				DynamicInteger integer = DynamicInteger.getNew(routines, einfo);
				if(integer == null) return null;
				
				ModDamage.addToLogRecord(OutputPreset.INFO, "Tag: " + matcher.group(1) + ", " + matcher.group(2) + ", " + integer.toString());
				return new Tag(matcher.group(), matcher.group(2).toLowerCase(), entityRef, integer);
			}
			return null;
		}
	}
}
