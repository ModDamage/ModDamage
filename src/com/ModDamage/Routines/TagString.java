package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.MessageAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class TagString extends NestedRoutine
{
	private final String tag;
	private final IDataProvider<Entity> entityDP;
	private final IDataProvider<World> worldDP;
	private final IDataProvider<String> value;
	
	protected TagString(String configString, String tag, IDataProvider<Entity> entityDP, IDataProvider<World> worldDP, IDataProvider<String> value)
	{
		super(configString);
		this.tag = tag;
		this.entityDP = entityDP;
		this.worldDP = worldDP;
		this.value = value;
	}
	
	protected TagString(String configString, String tag, IDataProvider<Entity> entityDP, IDataProvider<World> worldDP)
	{
		super(configString);
		this.tag = tag;
		this.entityDP = entityDP;
		this.worldDP = worldDP;
		this.value = null;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		if (worldDP != null)
		{
			World world = worldDP.get(data);
			if (world == null)
				return;
			
			if(value != null)
			{
				String oldTagValue = ModDamage.getTagger().stringTags.getTagValue(world, tag);
				EventData myData = myInfo.makeChainedData(data, oldTagValue == null? "" : oldTagValue);
				ModDamage.getTagger().stringTags.addTag(world, tag, value.get(myData));
			}
			else
				ModDamage.getTagger().stringTags.removeTag(world, tag);
		}
		else
		{
			Entity entity = entityDP.get(data);
			if(entity == null)
				return;
			
			if(value != null)
			{
				String oldTagValue = ModDamage.getTagger().stringTags.getTagValue(entity, tag);
				EventData myData = myInfo.makeChainedData(data, oldTagValue == null? "" : oldTagValue);
				ModDamage.getTagger().stringTags.addTag(entity, tag, value.get(myData));
			}
			else
				ModDamage.getTagger().stringTags.removeTag(entity, tag);
		}
	}

	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("unstag\\.(\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("stag\\.(\\w+)\\.(\\w+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	public static final EventInfo myInfo = new SimpleEventInfo(String.class, "value", "-default");
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public TagString getNew(Matcher matcher, EventInfo info)
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
			
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "UnSTag: " + matcher.group(1) + ", " + matcher.group(2));
			return new TagString(matcher.group(), matcher.group(2).toLowerCase(), entityDP, worldDP);
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public TagString getNew(Matcher matcher, Object nestedContent, EventInfo info)
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

			
			EventInfo einfo = info.chain(myInfo);
			
			String string;
			
			if (nestedContent instanceof String)
				string = (String) nestedContent;
			else {
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Only one string allowed for stag");
				return null;
			}

			InterpolatedString value = MessageAliaser.match(string, einfo).iterator().next();
			if(value == null) return null;
			

			ModDamage.addToLogRecord(OutputPreset.INFO, "STag: " + matcher.group(1) + ", " + matcher.group(2) + ": \"" + value + "\"");
			
			
			return new TagString(matcher.group(), matcher.group(2).toLowerCase(), entityDP, worldDP, value);
		}
	}
}
