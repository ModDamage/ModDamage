package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import org.bukkit.Location;

import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Expressions.IntegerExp;
import com.ModDamage.Routines.Routines;

public class Explode extends NestedRoutine
{
	private final IDataProvider<Location> locDP;
	private final IDataProvider<Integer> strength;
	
	public Explode(String configString, IDataProvider<Location> locDP, IDataProvider<Integer> strength)
	{
		super(configString);
		this.locDP = locDP;
		this.strength = strength;
	}

	static final EventInfo myInfo = new SimpleEventInfo(Integer.class, "strength", "-default");

	@Override
	public void run(EventData data) throws BailException
	{
		Location entity = locDP.get(data);
		if (entity == null) return;

		EventData myData = myInfo.makeChainedData(data, 0);
		entity.getWorld().createExplosion(entity, strength.get(myData)/10.0f);
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.*?)(?:effect)?\\.explode", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
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

			IDataProvider<Integer> strength = IntegerExp.getNew(routines, einfo);
			if(strength == null) return null;

			return new Explode(matcher.group(), locDP, strength);
		}
	}
}
