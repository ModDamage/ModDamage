package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Events.Repeat;
import com.ModDamage.Variables.Int.Constant;

public class RepeatControl extends Routine
{
	private final IDataProvider<Entity> entityDP;
	private final String repeatName;
	private final IDataProvider<Integer> delay, count;
	
	protected RepeatControl(String configString, IDataProvider<Entity> entityDP, String repeatName,
			IDataProvider<Integer> delay, IDataProvider<Integer> count)
	{
		super(configString);
		this.entityDP = entityDP;
		this.repeatName = repeatName;
		this.delay = delay;
		this.count = count;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		if (entity == null) return;

		if (delay == null)
		{
			Repeat.stop(repeatName, entity);
			return;
		}
		
		Repeat.start(repeatName, entity, delay.get(data), count.get(data));
	}

	public static void register()
	{
		Routine.registerRoutine(
				Pattern.compile("(.+?)\\.(?:start(?:repeat)?\\.(\\w+)\\.(.+)|stop(?:repeat)?\\.(\\w+))", Pattern.CASE_INSENSITIVE),
				new RoutineBuilder());
	}
	
	private static Pattern dotPattern = Pattern.compile("\\s*\\.\\s*");

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public RepeatControl getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			IDataProvider<Integer> delay, count;
			
			
			String repeatName = matcher.group(2);
			if (repeatName == null)
			{
				repeatName = matcher.group(4);
				if (repeatName == null)
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "No repeat name?");
					return null;
				}
				
				delay = null;
				count = null;
			}
			else
			{
				StringMatcher sm = new StringMatcher(matcher.group(3));
				delay = DataProvider.parse(info, Integer.class, sm.spawn()); if (delay == null) return null;
				if (sm.matchesFront(dotPattern))
				{
					count = DataProvider.parse(info, Integer.class, sm.spawn()); if (count == null) return null;
				}
				else
					count = new Constant(-1);
				if (!sm.isEmpty()) return null;
			}
			
			if (delay != null)
				ModDamage.addToLogRecord(OutputPreset.INFO, "Start Repeat: on " + entityDP + " named \""+repeatName+"\" delay " + delay + " count " + count);
			else
				ModDamage.addToLogRecord(OutputPreset.INFO, "Stop Repeat: on " + entityDP + " named \""+repeatName+"\"");
			return new RepeatControl(matcher.group(), entityDP, repeatName, delay, count);
		}
	}
}
