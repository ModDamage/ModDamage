package com.moddamage.routines.nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import com.moddamage.LogUtil;
import com.moddamage.ModDamage;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

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
			
			LogUtil.info("Delay: \"" + numberMatch + "\"");
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
