package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import org.bukkit.Location;

import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.NumberExp;
import com.ModDamage.Routines.Routines;

public class Explode extends NestedRoutine
{
	private final IDataProvider<Location> locDP;
	private final IDataProvider<Number> strength;
	private final boolean fire;

	public Explode(String configString, IDataProvider<Location> locDP, IDataProvider<Number> strength, boolean fire)
	{
		super(configString);
		this.locDP = locDP;
		this.strength = strength;
		this.fire = fire;
	}

	static final EventInfo myInfo = new SimpleEventInfo(Number.class, "strength", "-default");

	@Override
	public void run(EventData data) throws BailException
	{
		Location entity = locDP.get(data);
		if (entity == null) return;

		EventData myData = myInfo.makeChainedData(data, 0);
		
		Number str = strength.get(myData);
		if (str == null) return;
		
		entity.getWorld().createExplosion(entity, str.floatValue(), fire);
	}

	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*?)(?:effect)?\\.explode(\\.withfire)?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{	
		@Override
		public Explode getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			IDataProvider<Location> locDP = DataProvider.parse(info, Location.class, matcher.group(1));
			if (locDP == null) return null;

			ModDamage.addToLogRecord(OutputPreset.INFO, "Explode at " + locDP + ":");

			EventInfo einfo = info.chain(myInfo);
			Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
			if (routines == null) return null;

			IDataProvider<Number> strength = NumberExp.getNew(routines, einfo);
			if(strength == null) return null;

			return new Explode(matcher.group(), locDP, strength, matcher.group(2) != null);
		}
	}
}
