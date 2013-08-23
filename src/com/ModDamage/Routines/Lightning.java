package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.ModDamage.ModDamage;
import com.ModDamage.MDLogger.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

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
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Lightning"+(effect?" effect":"")+" at " + locDP);
			return new RoutineBuilder(new Lightning(scriptLine, locDP, effect));
		}
	}
}
