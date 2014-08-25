package com.moddamage.routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.moddamage.LogUtil;
import com.moddamage.backend.BailException;
import com.moddamage.backend.ScriptLine;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataProvider;

public class Lightning extends Routine
{
	private final IDataProvider<Location> locDP;
    private final boolean effect;

	protected Lightning(ScriptLine scriptLine, IDataProvider<Location> locDP, boolean effect)
	{
		super(scriptLine);
		this.locDP = locDP;
        this.effect = effect;
    }

	@Override
	public void run(EventData data) throws BailException
	{
		Location loc = locDP.get(data);
        if (loc == null) return;

        if (effect)
            loc.getWorld().strikeLightningEffect(loc);
        else
            loc.getWorld().strikeLightning(loc);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.+?)\\.lightning(effect)?", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{ 
			IDataProvider<Location> locDP = DataProvider.parse(info, Location.class, matcher.group(1));
			if (locDP == null) return null;

            boolean effect = matcher.group(2) != null;
			
            LogUtil.info("Lightning"+(effect?" effect":"")+" at " + locDP);
			return new RoutineBuilder(new Lightning(scriptLine, locDP, effect));
		}
	}
}
