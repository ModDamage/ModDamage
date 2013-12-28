package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.ScriptLine;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;

public class Explode extends Routine
{
	private final IDataProvider<Location> locDP;
	private final IDataProvider<Number> strength;
	private final boolean fire;

	public Explode(ScriptLine scriptLine, IDataProvider<Location> locDP, IDataProvider<Number> strength, boolean fire)
	{
		super(scriptLine);
		this.locDP = locDP;
		this.strength = strength;
		this.fire = fire;
	}
	@Override
	public void run(EventData data) throws BailException
	{
		Location entity = locDP.get(data);
		if (entity == null) return;
		
		Number str = strength.get(data);
		if (str == null) return;
		
		entity.getWorld().createExplosion(entity, str.floatValue(), fire);
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(.*?)(?:effect)?\\.explode(\\.withfire)?(?::|\\s+by\\s|\\s)\\s*(.+)", Pattern.CASE_INSENSITIVE), new RoutineFactory());
	}

	protected static class RoutineFactory extends Routine.RoutineFactory
	{
		@Override
		public IRoutineBuilder getNew(Matcher matcher, ScriptLine scriptLine, EventInfo info)
		{
			IDataProvider<Location> locDP = DataProvider.parse(info, Location.class, matcher.group(1));
			if (locDP == null) return null;

			IDataProvider<Number> strength = DataProvider.parse(info, Number.class, matcher.group(2));
			if(strength == null) return null;

			ModDamage.addToLogRecord(OutputPreset.INFO, "Explode at " + locDP + " with strength " + strength);

			return new RoutineBuilder(new Explode(scriptLine, locDP, strength, matcher.group(2) != null));
		}
	}
}
