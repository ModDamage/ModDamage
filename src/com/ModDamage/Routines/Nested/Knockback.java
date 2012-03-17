package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.RoutineAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.Backend.IntRef;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;
//FIXME Do I work?
public class Knockback extends NestedRoutine
{	
	protected final DataRef<Entity> entityRef;
	protected final DataRef<Entity> entityOtherRef;
	
	protected final Routines routines;

	protected Knockback(String configString, DataRef<Entity> entityRef, DataRef<Entity> entityOtherRef, Routines routines)
	{
		super(configString);
		this.entityRef = entityRef;
		this.entityOtherRef = entityOtherRef;
		this.routines = routines;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			IntRef.class, "x", "horiz", "-default",
			IntRef.class, "y", "vertical");
	
	@Override
	public void run(EventData data) throws BailException
	{
		Entity firstEntity = entityRef.get(data);
		Entity secondEntity = entityOtherRef.get(data);
		if(secondEntity == null) return;
		
		Vector vector = firstEntity.getLocation().toVector().subtract(secondEntity.getLocation().toVector());
		
		IntRef xRef = new IntRef(0);
		IntRef yRef = new IntRef(0);
		
		routines.run(myInfo.makeChainedData(data, xRef, yRef));
		
	
		double hLength = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getZ(), 2));
		
		double horizValue = ((float)xRef.value) / 10.0;
		double verticalValue = ((float)yRef.value) / 10.0;
		
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
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			if (entityRef == null) return null;
			
			boolean explicitFrom = matcher.group(2) != null;
			String otherName;
			if (explicitFrom)
				otherName = matcher.group(2).toLowerCase();
			else
				otherName = "-" + matcher.group(1).toLowerCase() + "-other";
			DataRef<Entity> entityOtherRef = info.get(Entity.class, otherName, explicitFrom);
			if (entityOtherRef == null)
			{
				if (!explicitFrom)
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "The entity '"+entityRef+"' doesn't have a natural opposite, so you need to specify one using '"+matcher.group()+".from.{entity}'");
				return null;
			}

			Routines routines = RoutineAliaser.parseRoutines(nestedContent, info.chain(myInfo));
			if (routines == null) return null;
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack: " + entityRef + " from " + entityOtherRef);
			return new Knockback(matcher.group(), entityRef, entityOtherRef, routines);

		}
	}

}
