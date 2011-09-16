package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DelayedRoutine extends Routine
{	
	private final long delayValue;
	private final List<Routine> routines;
	public DelayedRoutine(String configString, long delayValue, List<Routine> routines)
	{
		super(configString);
		this.delayValue = delayValue;
		this.routines = routines;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		TargetEventInfo.server.getScheduler().scheduleAsyncDelayedTask(TargetEventInfo.server.getPluginManager().getPlugin("ModDamage"), new DelayedRunnable(eventInfo, routines), delayValue);
	}
		
	public static void register(ModDamage routineUtility)
	{
		Routine.registerBase(DelayedRoutine.class, Pattern.compile("delay\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static DelayedRoutine getNew(Matcher matcher, List<Routine> routines)
	{ 
		if(matcher != null)
		{
			return new DelayedRoutine(matcher.group(), Long.parseLong(matcher.group(1)), routines);
		}
		return null;
	}
	
	private class DelayedRunnable implements Runnable
	{
		private final TargetEventInfo eventInfo;
		private final List<Routine> routines;
		private DelayedRunnable(TargetEventInfo eventInfo, List<Routine> routines)
		{
			this.eventInfo = eventInfo;
			this.routines = routines;
		}
		
		@Override
		public void run() //Runnable
		{
			for(Routine routine : routines)
				routine.run(eventInfo);
		}
	}
}
