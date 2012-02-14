package com.ModDamage.Routines.Nested.Parameterized;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.SimpleEventInfo;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;
//FIXME Do I work?
public class Knockback extends ParameterizedRoutine
{	
	protected final DataRef<Entity> entityRef;
	protected final DataRef<Entity> entityOtherRef;
	protected final boolean usingParameterized;
	
	protected final DynamicInteger strength;
	protected final DynamicInteger horizInteger, verticalInteger;

	protected Knockback(String configString, Routines routines, EventInfo info, DataRef<Entity> entityRef, DataRef<Entity> entityOtherRef)
	{
		super(configString);
		this.usingParameterized = false;
		this.entityRef = entityRef;
		this.entityOtherRef = entityOtherRef;
		this.strength = DynamicInteger.getNew(routines, info);
		horizInteger = verticalInteger = null;
	}


	protected Knockback(String configString, DynamicInteger xInteger, DynamicInteger yInteger, DataRef<Entity> entityRef, DataRef<Entity> entityOtherRef)
	{
		super(configString);
		this.usingParameterized = true;
		this.entityRef = entityRef;
		this.entityOtherRef = entityOtherRef;
		this.strength = null;
		this.horizInteger = xInteger;
		this.verticalInteger = yInteger;
	}
	
	static final EventInfo myInfo = new SimpleEventInfo(
			Integer.class, "strength", "-default");
	
	@Override
	public void run(EventData data)
	{
		Entity firstEntity = entityRef.get(data);
		Entity secondEntity = entityOtherRef.get(data);
		if(secondEntity == null) return;
		
		Vector vector = firstEntity.getLocation().toVector().subtract(secondEntity.getLocation().toVector());
		
		EventData myData = myInfo.makeChainedData(data, 0);
		
		if(usingParameterized)
		{
			double length = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getZ(), 2));
			
			double horizValue = ((float)horizInteger.getValue(myData)) / 10.0;
			myData.objects[0] = 0;
			double verticalValue = ((float)verticalInteger.getValue(myData)) / 10.0;
			
			firstEntity.setVelocity(firstEntity.getVelocity().add(
					new Vector(vector.getX() / length * horizValue, verticalValue, vector.getZ() / length * horizValue)));
		}
		else
		{
			firstEntity.setVelocity(firstEntity.getVelocity().add(vector.multiply(strength.getValue(data))));
		}
	}
	
	public static void register(){ NestedRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.knockback", Pattern.CASE_INSENSITIVE), new RoutineBuilder()); }
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Knockback getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase()); if (entityRef == null) return null;
			DataRef<Entity> entityOtherRef = info.get(Entity.class, matcher.group(1).toLowerCase().concat("-other")); if (entityOtherRef == null) return null;

			EventInfo einfo = info.chain(myInfo);
			if(nestedContent instanceof LinkedHashMap)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack (parameterized): ");
				
				List<DynamicInteger> integers = new ArrayList<DynamicInteger>();
				LinkedHashMap<String, Object> map = ModDamage.getPluginConfiguration().castToStringMap(
						"Knockback routine", nestedContent);
				
				if(ParameterizedRoutine.getRoutineParameters(integers, map, einfo, "X", "Y"))
					return new Knockback(matcher.group(), integers.get(0), integers.get(1), entityRef, entityOtherRef);
				else
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "");
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack: ");
				Routines routines = RoutineAliaser.parseRoutines(nestedContent, einfo);
				if(routines != null)
					return new Knockback(matcher.group(), routines, info, entityRef, entityOtherRef);
			}
			return null;
		}
	}

}
