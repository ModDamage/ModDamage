package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DelayedRoutine extends NestedRoutine
{	
	protected final IntegerMatch delay;
	protected final List<Routine> routines;
	protected static final Pattern delayPattern = Pattern.compile("delay\\." + Routine.dynamicIntegerPart, Pattern.CASE_INSENSITIVE);
	public DelayedRoutine(String configString, IntegerMatch delayValue, List<Routine> routines)
	{
		super(configString);
		this.delay = delayValue;
		this.routines = routines;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		TargetEventInfo.server.getScheduler().scheduleAsyncDelayedTask(TargetEventInfo.server.getPluginManager().getPlugin("ModDamage"), new DelayedRunnable(eventInfo), delay.getValue(eventInfo));
	}
		
	public static void register()
	{
		Routine.registerBase(DelayedRoutine.class, delayPattern);
	}
	
	public static DelayedRoutine getNew(String string, Object nestedContent)
	{ 
		if(string != null && nestedContent != null)
		{
			Matcher matcher = delayPattern.matcher(string);
			if(matcher.matches())
			{
				ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "Delay: \"" + matcher.group() + "\"", LoadState.SUCCESS);
				
				LoadState[] stateMachine = { LoadState.SUCCESS };
				List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
				if(!stateMachine[0].equals(LoadState.FAILURE))
				{
					IntegerMatch numberMatch = IntegerMatch.getNew(matcher.group(1));
					if(numberMatch != null)
					{
						ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Delay \"" + matcher.group() + "\"\n", LoadState.SUCCESS);
						return new DelayedRoutine(matcher.group(), numberMatch, routines);
					}
					else ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid Delay \"" + matcher.group() + "\"", LoadState.FAILURE);
				}
			}
		}
		return null;
	}
	
	private class DelayedRunnable implements Runnable
	{
		private final TargetEventInfo eventInfo;
		private DelayedRunnable(TargetEventInfo eventInfo)
		{
			this.eventInfo = eventInfo;
		}
		
		@Override
		public void run()//Runnable
		{
			for(Routine routine : routines)
				routine.run(eventInfo);
		}
	}
}
