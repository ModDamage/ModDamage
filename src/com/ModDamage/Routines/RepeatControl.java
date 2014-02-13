package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.ModDamage.LogUtil;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Events.Repeat;
import com.ModDamage.Expressions.LiteralNumber;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class RepeatControl extends Routine
{
	@SuppressWarnings("rawtypes")
	private final IDataProvider itDP;
	private final String repeatName;
	private final IDataProvider<? extends Number> delay, count;
	
	@SuppressWarnings("rawtypes")
	protected RepeatControl(ScriptLine scriptLine, IDataProvider itDP, String repeatName,
			IDataProvider<? extends Number> delay, IDataProvider<? extends Number> count)
	{
		super(scriptLine);
		this.itDP = itDP;
		this.repeatName = repeatName;
		this.delay = delay;
		this.count = count;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Object it = itDP.get(data);
		if (it == null) return;

		if (delay == null)
		{
			Repeat.stop(repeatName, it);
			return;
		}
		
		Number del = delay.get(data);
		Number c = count.get(data);
		if (del == null || c == null) return;
		
		Repeat.start(repeatName, it, del.intValue(), c.intValue());
	}

	public static void register()
	{
		Routine.registerRoutine(
				Pattern.compile("(.+?)\\.(?:start(?:repeat)?\\.(\\w+)\\.(.+)|stop(?:repeat)?[\\.,](\\w+))", Pattern.CASE_INSENSITIVE),
				new RoutineFactory());
	}
	
	private static Pattern dotPattern = Pattern.compile("\\s*\\.\\s*");

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			@SuppressWarnings("rawtypes")
			IDataProvider itDP = DataProvider.parse(info, Entity.class, matcher.group(1), true, false);
			if (itDP == null)
				itDP = DataProvider.parse(info, Location.class, matcher.group(1), true, false);
			if (itDP == null)
				itDP = DataProvider.parse(info, Chunk.class, matcher.group(1), true, false);
			if (itDP == null)
				itDP = DataProvider.parse(info, World.class, matcher.group(1), true, false);
			if (itDP == null) return null;
			
			IDataProvider<? extends Number> delay, count;
			
			
			String repeatName = matcher.group(2);
			if (repeatName == null)
			{
				repeatName = matcher.group(4);
				if (repeatName == null)
				{
					LogUtil.error("No repeat name?");
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
					count = new LiteralNumber(-1);
				if (!sm.isEmpty()) return null;
			}
			
			if (delay != null)
				LogUtil.info("Start Repeat: on " + itDP + " named \""+repeatName+"\" delay " + delay + " count " + count);
			else
				LogUtil.info("Stop Repeat: on " + itDP + " named \""+repeatName+"\"");
			return new RoutineBuilder(new RepeatControl(scriptLine, itDP, repeatName, delay, count));
		}
	}
}
