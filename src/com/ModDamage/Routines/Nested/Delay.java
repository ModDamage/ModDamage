package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Routines.Routines;

public class Delay extends NestedRoutine
{	
	protected final IDataProvider<Integer> delay;
	protected final Routines routines;
	protected static final Pattern delayPattern = Pattern.compile("delay\\.(.*)", Pattern.CASE_INSENSITIVE);
	public Delay(String configString, IDataProvider<Integer> delayValue, Routines routines)
	{
		super(configString);
		this.delay = delayValue;
		this.routines = routines;
	}
	@Override
	public void run(EventData data) throws BailException
	{
		DelayedRunnable dr = new DelayedRunnable(data.clone());
		Bukkit.getScheduler().scheduleSyncDelayedTask(ModDamage.getPluginConfiguration().plugin, dr, delay.get(data));
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
						IDataProvider<Integer> numberMatch = DataProvider.parse(info, Integer.class, matcher.group(1));
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
			try
			{
				routines.run(data);
			}
			catch (BailException e)
			{
				ModDamage.reportBailException(new BailException(Delay.this, e));
			}
		}
	}
}
