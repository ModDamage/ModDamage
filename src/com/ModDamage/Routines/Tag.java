package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.StringMatcher;
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
	private final IDataProvider<String> tagDP;
	private final IDataProvider<Entity> entityDP;
	private final IDataProvider<World> worldDP;
	private final IDataProvider<Integer> value;
	
	protected Tag(String configString, IDataProvider<String> tagDP, IDataProvider<Entity> entityDP, IDataProvider<World> worldDP, IDataProvider<Integer> value)
	{
		super(configString);
		this.tagDP = tagDP;
		this.entityDP = entityDP;
		this.worldDP = worldDP;
		this.value = value;
	}
	
	protected Tag(String configString, IDataProvider<String> tagDP, IDataProvider<Entity> entityDP, IDataProvider<World> worldDP)
	{
		super(configString);
        this.tagDP = tagDP;
		this.entityDP = entityDP;
		this.worldDP = worldDP;
		this.value = null;
	}

	@Override
	public void run(EventData data) throws BailException
	{
        String tag = tagDP.get(data);
        if (tag == null) return;
        tag = tag.toLowerCase();

		if (worldDP != null)
		{
			World world = worldDP.get(data);
			if (world == null)
				return;
			
			if(value != null)
			{
				Integer oldTagValue = ModDamage.getTagger().intTags.onWorld.getTagValue(world, tag);
				EventData myData = myInfo.makeChainedData(data, oldTagValue == null? 0 : oldTagValue);
				ModDamage.getTagger().intTags.onWorld.addTag(world, tag, value.get(myData));
			}
			else
				ModDamage.getTagger().intTags.onWorld.removeTag(world, tag);
		}
		else
		{
			Entity entity = entityDP.get(data);
			if(entity == null)
				return;
			
			if(value != null)
			{
				Integer oldTagValue = ModDamage.getTagger().intTags.onEntity.getTagValue(entity, tag);
				EventData myData = myInfo.makeChainedData(data, oldTagValue == null? 0 : oldTagValue);
				ModDamage.getTagger().intTags.onEntity.addTag(entity, tag, value.get(myData));
			}
			else
				ModDamage.getTagger().intTags.onEntity.removeTag(entity, tag);
		}
	}

	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("untag\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("tag\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	public static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "value", "-default");
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
            IDataProvider<String> tagDP = InterpolatedString.parseWord(InterpolatedString.word, new StringMatcher(matcher.group(2)), info);
            if (tagDP == null) return null;

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
			return new Tag(matcher.group(), tagDP, entityDP, worldDP);
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Tag getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			String name = matcher.group(1).toLowerCase();
            IDataProvider<String> tagDP = InterpolatedString.parseWord(InterpolatedString.word, new StringMatcher(matcher.group(2)), info);
            if (tagDP == null) return null;

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

			ModDamage.addToLogRecord(OutputPreset.INFO, "Tag: " + name + ", " + tagDP);
			
			ModDamage.changeIndentation(true);
			
			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if(routines == null) return null;

			IDataProvider<Integer> value = IntegerExp.getNew(routines, einfo);
			if(value == null) return null;
			
			ModDamage.changeIndentation(false);
			
			
			return new Tag(matcher.group(), tagDP, entityDP, worldDP, value);
		}
	}
}
