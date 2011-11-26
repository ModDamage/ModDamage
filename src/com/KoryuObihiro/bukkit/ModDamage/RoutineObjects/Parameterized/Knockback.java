package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
//FIXME Do I work?
public class Knockback extends ParameterizedIntegerRoutine
{	
	protected final EntityReference entityReference;
	protected final boolean usingParameterized;

	protected Knockback(String configString, List<Routine> routines, EntityReference entityReference)
	{
		super(configString, Arrays.asList(DynamicInteger.getNew(routines)));
		this.usingParameterized = false;
		this.entityReference = entityReference;
	}


	protected Knockback(String configString, DynamicInteger xReference, DynamicInteger yReference, EntityReference entityReference)
	{
		super(configString, Arrays.asList(xReference, yReference));
		this.usingParameterized = true;
		this.entityReference = entityReference;
	}

	@Override
	public void run(TargetEventInfo eventInfo)
	{
		int temp = eventInfo.eventValue;
		Entity firstEntity = entityReference.getEntity(eventInfo);
		Entity secondEntity = entityReference.getEntityOther(eventInfo);
		
		double relativeXDiff = 0, relativeZDiff = 0;
		if(secondEntity != null)
		{
			relativeXDiff = (firstEntity.getLocation().getX() - secondEntity.getLocation().getX());
			relativeZDiff = (firstEntity.getLocation().getZ() - secondEntity.getLocation().getZ());
		}
		
		else
		{
			Vector vector = firstEntity.getLocation().getDirection();
			relativeXDiff = vector.getX();
			relativeZDiff = relativeXDiff = vector.getZ();
		}

		int firstIntegerValue = integers.get(0).getValue(eventInfo);
		eventInfo.eventValue = temp;
		
		if(usingParameterized)
		{
			Vector vector = firstEntity.getVelocity().add(new Vector(relativeXDiff * firstIntegerValue, integers.get(1).getValue(eventInfo), relativeZDiff * firstIntegerValue));
			firstEntity.setVelocity(vector);			
			eventInfo.eventValue = temp;
		}
		else if(secondEntity != null)
		{
			double multiplier = Math.abs(firstEntity.getLocation().getX() - secondEntity.getLocation().getX()) + Math.abs(firstEntity.getLocation().getY() - secondEntity.getLocation().getY()) + Math.abs(firstEntity.getLocation().getZ() - secondEntity.getLocation().getZ());
			multiplier *= multiplier;
			multiplier = firstIntegerValue / multiplier;
			
			int relativeYDiff = (int)(firstEntity.getLocation().getY() - secondEntity.getLocation().getY());
			
			relativeXDiff *= relativeXDiff != 0?relativeXDiff * (relativeXDiff/Math.abs(relativeXDiff)):0;
			relativeYDiff *= relativeYDiff != 0?relativeYDiff * (relativeYDiff/Math.abs(relativeYDiff)):0;
			relativeZDiff *= relativeZDiff != 0?relativeZDiff * (relativeZDiff/Math.abs(relativeZDiff)):0;
			
			Vector vector = firstEntity.getVelocity().add(new Vector(relativeXDiff * multiplier, relativeYDiff * multiplier, relativeZDiff * multiplier));
			firstEntity.setVelocity(vector);//TODO Use some trig here?
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
				if(ParameterizedIntegerRoutine.getRoutineParameters(integers, ModDamage.getPluginConfiguration().castToStringMap("Knockback routine", nestedContent), "X", "Y") && reference != null)
					return new Knockback(matcher.group(), integers.get(0), integers.get(1), reference);
				else ModDamage.addToLogRecord(OutputPreset.FAILURE, "");
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack: ");
				List<Routine> routines = new ArrayList<Routine>();
				if(RoutineAliaser.parseRoutines(routines, nestedContent) && reference != null)
					return new Knockback(matcher.group(), routines, reference);
			}
			return null;
		}
	}

}
