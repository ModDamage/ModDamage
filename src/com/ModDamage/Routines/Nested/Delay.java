package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class Delay extends NestedRoutine
{	
	protected final IDataProvider<Integer> delay;
	protected static final Pattern delayPattern = Pattern.compile("delay(?:\\.|\\s+)(.*)", Pattern.CASE_INSENSITIVE);
	public Delay(ScriptLine scriptLine, IDataProvider<Integer> delayValue)
	{
		super(scriptLine);
		this.delay = delayValue;
	}
	@Override
	public void run(EventData data) throws BailException
	{
		Integer del = delay.get(data);
		if (del == null) return;
		
		DelayedRunnable dr = new DelayedRunnable(data.clone());
		Bukkit.getScheduler().scheduleSyncDelayedTask(ModDamage.getPluginConfiguration().plugin, dr, del);
	}
		
	public static void register(){ NestedRoutine.registerRoutine(delayPattern, new RoutineFactory()); }
	protected static class RoutineFactory extends NestedRoutine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			if(!matcher.matches()) return null;

			IDataProvider<Integer> numberMatch = DataProvider.parse(info, Integer.class, matcher.group(1));
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Delay: \"" + numberMatch + "\"");
			if (numberMatch == null) return null;

			Delay routine = new Delay(scriptLine, numberMatch);
			return new NestedRoutineBuilder(routine, routine.routines, info);
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
		public void run()
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
