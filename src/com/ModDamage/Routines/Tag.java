package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class Tag extends NestedRoutine
{
	private final String tag;
	private final IDataProvider<Entity> entityDP;
	private final IDataProvider<World> worldDP;
	private final IDataProvider<Integer> integer;
	
	protected Tag(String configString, String tag, IDataProvider<Entity> entityDP, IDataProvider<World> worldDP, IDataProvider<Integer> integer)
	{
		super(configString);
		this.tag = tag;
		this.entityDP = entityDP;
		this.worldDP = worldDP;
		this.integer = integer;
	}
	
	protected Tag(String configString, String tag, IDataProvider<Entity> entityDP, IDataProvider<World> worldDP)
	{
		super(configString);
		this.tag = tag;
		this.entityDP = entityDP;
		this.worldDP = worldDP;
		this.integer = null;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		if (worldDP != null)
		{
			World world = worldDP.get(data);
			if (world == null)
				return;
			
			if(integer != null)
			{
				Integer oldTagValue = ModDamage.getTagger().intTags.getTagValue(world, tag);
				EventData myData = myInfo.makeChainedData(data, oldTagValue == null? 0 : oldTagValue);
				ModDamage.getTagger().intTags.addTag(world, tag, integer.get(myData));
			}
			else
				ModDamage.getTagger().intTags.removeTag(world, tag);
		}
		else
		{
			Entity entity = entityDP.get(data);
			if(entity == null)
				return;
			
			if(integer != null)
			{
				Integer oldTagValue = ModDamage.getTagger().intTags.getTagValue(entity, tag);
				EventData myData = myInfo.makeChainedData(data, oldTagValue == null? 0 : oldTagValue);
				ModDamage.getTagger().intTags.addTag(entity, tag, integer.get(myData));
			}
			else
				ModDamage.getTagger().intTags.removeTag(entity, tag);
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
	
	public static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "value", "-default");
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<Entity> entityDP = null;
			IDataProvider<World> worldDP = null;
			
			if (name.equals("world"))
			{
				worldDP = DataProvider.parse(info, World.class, name);
				if(worldDP == null) return null;
			}
			else
			{
				entityDP = DataProvider.parse(info, Entity.class, name);
				if(entityDP == null) return null;
			}
			
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Untag: " + matcher.group(1) + ", " + matcher.group(2));
			return new Tag(matcher.group(), matcher.group(2).toLowerCase(), entityDP, worldDP);
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
			IDataProvider<Entity> entityDP = null;
			IDataProvider<World> worldDP = null;
			
			if (name.equals("world"))
			{
				worldDP = DataProvider.parse(info, World.class, name);
				if(worldDP == null) return null;
			}
			else
			{
				entityDP = DataProvider.parse(info, Entity.class, name);
				if(entityDP == null) return null;
			}

			ModDamage.addToLogRecord(OutputPreset.INFO, "Tag: " + matcher.group(1) + ", " + matcher.group(2));
			
			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines == null) return null;

			IDataProvider<Integer> integer = IntegerExp.getNew(routines, einfo);
			if(integer == null) return null;
			
			
			return new Tag(matcher.group(), matcher.group(2).toLowerCase(), entityDP, worldDP, integer);
		}
	}
}
