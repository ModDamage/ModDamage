package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;
//FIXME Do I work?
public class Knockback extends NestedRoutine
{	
	protected final IDataProvider<Entity> entityDP;
	protected final IDataProvider<Entity> entityOtherDP;
	
	protected final Routines routines;

	protected Knockback(String configString, IDataProvider<Entity> entityDP, IDataProvider<Entity> entityOtherDP, Routines routines)
	{
		super(configString);
		this.entityDP = entityDP;
		this.entityOtherDP = entityOtherDP;
		this.routines = routines;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Integer.class, "x", "horiz", "-default",
			Integer.class, "y", "vertical");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Entity firstEntity = entityDP.get(data);
		Entity secondEntity = entityOtherDP.get(data);
		if(firstEntity == null || secondEntity == null) return;
		
		Vector vector = firstEntity.getLocation().toVector().subtract(secondEntity.getLocation().toVector());
		
		EventData myData = myInfo.makeChainedData(data, 0, 0);
		
		routines.run(myData);
		
		int xRef = myData.get(Integer.class, myData.start + 0);
		int yRef = myData.get(Integer.class, myData.start + 1);
		
	
		double hLength = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getZ(), 2));
		
		double horizValue = ((float)xRef) / 10.0;
		double verticalValue = ((float)yRef) / 10.0;
		
		firstEntity.setVelocity(firstEntity.getVelocity().add(
				new Vector(vector.getX() / hLength * horizValue, verticalValue, vector.getZ() / hLength * horizValue)));
	}
	
	public static void register()
	{
		NestedRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.knockback(?:\\.from\\.(\\w+))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Knockback getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, matcher.group(1));
			if (entityDP == null) return null;
			
			boolean explicitFrom = matcher.group(2) != null;
			IDataProvider<Entity> entityOtherDP;
			if (explicitFrom) {
				entityOtherDP = DataProvider.parse(info, Entity.class, matcher.group(2));
				if (entityOtherDP == null)
					return null;
			} else {
				entityOtherDP = info.get(Entity.class, "-" + matcher.group(1).toLowerCase() + "-other", false);
				if (entityOtherDP == null)
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "The entity '"+entityDP+"' doesn't have a natural opposite, so you need to specify one using '"+matcher.group()+".from.{entity}'");
					return null;
				}
			}

            ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack " + entityDP + " from " + entityOtherDP);

			Routines routines = RoutineAliaser.parseRoutines(nestedContent, info.chain(myInfo));
			if (routines == null) return null;

			return new Knockback(matcher.group(), entityDP, entityOtherDP, routines);

		}
	}

}
