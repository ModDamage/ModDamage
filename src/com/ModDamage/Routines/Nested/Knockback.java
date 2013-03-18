package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;
//FIXME Do I work?
public class Knockback extends NestedRoutine
{	
	protected final IDataProvider<Entity> entityDP;
	protected final IDataProvider<Location> fromDP;
	
	protected final Routines routines;

	protected Knockback(String configString, IDataProvider<Entity> entityDP, IDataProvider<Location> fromDP, Routines routines)
	{
		super(configString);
		this.entityDP = entityDP;
		this.fromDP = fromDP;
		this.routines = routines;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Number.class, "x", "horiz", "-default",
			Number.class, "y", "vertical");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityDP.get(data);
		Location from = fromDP.get(data);
		if(entity == null || from == null) return;
		
		Vector vector = entity.getLocation().toVector().subtract(from.toVector());

		EventData myData = myInfo.makeChainedData(data, 0, 0);
		
		routines.run(myData);
		
		double xRef = myData.get(Number.class, myData.start + 0).doubleValue();
		double yRef = myData.get(Number.class, myData.start + 1).doubleValue();
	
		double hLength = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getZ(), 2));
		
		double horizValue = ((float)xRef) / 10.0;
		double verticalValue = ((float)yRef) / 10.0;

        if (Math.abs(hLength) < 0.01) {
            hLength = 1;
            horizValue = 0;
        }
		
		entity.setVelocity(entity.getVelocity().add(
				new Vector(vector.getX() / hLength * horizValue, verticalValue, vector.getZ() / hLength * horizValue)));
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(.+?)(?:effect)?\\.knockback(?:\\.from\\.(.+))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Knockback getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			boolean explicitFrom = matcher.group(2) != null;
			IDataProvider<Location> fromDP = null;
			if (explicitFrom) {
                fromDP = DataProvider.parse(info, Location.class, matcher.group(2));
				if (fromDP == null)
					return null;
			} else {
                IDataProvider<Entity> otherEntityDP = info.get(Entity.class, "-" + matcher.group(1).toLowerCase() + "-other", false);
                if (otherEntityDP != null)
                    fromDP = DataProvider.transform(Location.class, otherEntityDP, info, false);
				if (fromDP == null)
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "The entity '"+entityDP+"' doesn't have a natural opposite, so you need to specify one using '"+matcher.group()+".from.{entity}'");
					return null;
				}
			}

            ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack " + entityDP + " from " + fromDP);

			Routines routines = RoutineAliaser.parseRoutines(nestedContent, info.chain(myInfo));
			if (routines == null) return null;

			return new Knockback(matcher.group(), entityDP, fromDP, routines);

		}
	}

}
