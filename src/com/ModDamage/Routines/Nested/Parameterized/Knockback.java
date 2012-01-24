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
import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.NestedRoutine;
//FIXME Do I work?
public class Knockback extends ParameterizedRoutine
{	
	protected final EntityReference entityReference;
	protected final boolean usingParameterized;
	
	protected final DynamicInteger strength;
	protected final DynamicInteger horizInteger, verticalInteger;

	protected Knockback(String configString, Routines routines, EntityReference entityReference)
	{
		super(configString);
		this.usingParameterized = false;
		this.entityReference = entityReference;
		this.strength = DynamicInteger.getNew(routines);
		horizInteger = verticalInteger = null;
	}


	protected Knockback(String configString, DynamicInteger xInteger, DynamicInteger yInteger, EntityReference entityReference)
	{
		super(configString);
		this.usingParameterized = true;
		this.entityReference = entityReference;
		this.strength = null;
		this.horizInteger = xInteger;
		this.verticalInteger = yInteger;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		Entity firstEntity = entityReference.getEntity(eventInfo);
		Entity secondEntity = entityReference.getEntityOther(eventInfo);
		
		Vector vector;
		if(secondEntity != null)
			vector = firstEntity.getLocation().toVector().subtract(secondEntity.getLocation().toVector());
		else
			vector = firstEntity.getLocation().getDirection();

		int temp = eventInfo.eventValue;
		
		if(usingParameterized)
		{
			double horizValue = ((float)horizInteger.getValue(eventInfo)) / 10.0;
			eventInfo.eventValue = temp;
			double verticalValue = ((float)verticalInteger.getValue(eventInfo)) / 10.0;
			eventInfo.eventValue = temp;
			
			firstEntity.setVelocity(firstEntity.getVelocity().add(
					new Vector(vector.getX() * horizValue, verticalValue, vector.getZ() * horizValue)));
		}
		else if(secondEntity != null)
		{
			firstEntity.setVelocity(firstEntity.getVelocity().add(vector.multiply(strength.getValue(eventInfo))));
		}
	}
	
	public static void register(){ NestedRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.knockback", Pattern.CASE_INSENSITIVE), new RoutineBuilder());}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Knockback getNew(Matcher matcher, Object nestedContent)
		{
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(nestedContent instanceof LinkedHashMap)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack (parameterized): ");
				
				List<DynamicInteger> integers = new ArrayList<DynamicInteger>();
				if(ParameterizedRoutine.getRoutineParameters(integers, ModDamage.getPluginConfiguration().castToStringMap("Knockback routine", nestedContent), "X", "Y") && reference != null)
					return new Knockback(matcher.group(), integers.get(0), integers.get(1), reference);
				else
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "");
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack: ");
				Routines routines = RoutineAliaser.parseRoutines(nestedContent);
				if(routines != null && reference != null)
					return new Knockback(matcher.group(), routines, reference);
			}
			return null;
		}
	}

}
