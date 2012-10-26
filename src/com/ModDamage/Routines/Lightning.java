package com.ModDamage.Routines;

import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lightning extends Routine
{
	private final IDataProvider<Location> locDP;
    private final boolean effect;

	protected Lightning(String configString, IDataProvider<Location> locDP, boolean effect)
	{
		super(configString);
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
		Routine.registerRoutine(Pattern.compile("(.+?)\\.lightning(effect)?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Lightning getNew(Matcher matcher, EventInfo info)
		{ 
			IDataProvider<Location> locDP = DataProvider.parse(info, Location.class, matcher.group(1));
			if (locDP == null) return null;

            boolean effect = matcher.group(2) != null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "Lightning"+(effect?" effect":"")+" at " + locDP);
			return new Lightning(matcher.group(), locDP, effect);
		}
	}
}
