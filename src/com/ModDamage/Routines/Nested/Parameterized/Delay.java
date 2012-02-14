package com.ModDamage.Routines.Nested.Parameterized;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;

public class Delay extends NestedRoutine
{	
	protected final DynamicInteger delay;
	protected final Routines routines;
	protected static final Pattern delayPattern = Pattern.compile("delay\\.(.*)", Pattern.CASE_INSENSITIVE);
	public Delay(String configString, DynamicInteger delayValue, Routines routines)
	{
		super(configString);
		this.delay = delayValue;
		this.routines = routines;
	}
	@Override
	public void run(EventData data)
	{
		DelayedRunnable dr = new DelayedRunnable(data.clone());
		Bukkit.getScheduler().scheduleAsyncDelayedTask(ModDamage.getPluginConfiguration().plugin, dr, delay.getValue(data));
	}
		
	public static void register(){ NestedRoutine.registerRoutine(delayPattern, new RoutineBuilder()); }
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Delay getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher != null && nestedContent != null)
			{
				if(matcher.matches())
				{
					ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
					ModDamage.addToLogRecord(OutputPreset.INFO, "Delay: \"" + matcher.group() + "\"");
					Routines routines = RoutineAliaser.parseRoutines(nestedContent, info);
					if(routines != null)
					{
						DynamicInteger numberMatch = DynamicInteger.getNew(matcher.group(1), info);
						if(numberMatch != null)
						{
							ModDamage.addToLogRecord(OutputPreset.INFO_VERBOSE, "End Delay \"" + matcher.group() + "\"\n");
							return new Delay(matcher.group(), numberMatch, routines);
						}
						else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid Delay \"" + matcher.group() + "\"");
					}
				}
			}
			return null;
		}
	}
	
	private class DelayedRunnable implements Runnable
	{
		private final EventData data;
		private DelayedRunnable(EventData data)
		{
			this.data = data;
		}
		
		@Override
		public void run()//Runnable
		{
			routines.run(data);
		}
	}
}
