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
public class Knockback extends ParameterizedRoutine
{
	protected static final int velocityMultiplier = 10;
	
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
		Entity firstEntity = entityReference.getEntity(eventInfo);
		Entity secondEntity = entityReference.getEntityOther(eventInfo);
		
		int relativeXDiff = (int)(firstEntity.getLocation().getX() - secondEntity.getLocation().getX());
		int relativeZDiff = (int)(firstEntity.getLocation().getZ() - secondEntity.getLocation().getZ());

		int firstIntegerValue = integers.get(0).getValue(eventInfo);
		
		if(usingParameterized)
		{
			//make these masks
			relativeXDiff /= Math.abs(relativeXDiff);
			relativeZDiff /= Math.abs(relativeZDiff);
			firstEntity.setVelocity(firstEntity.getVelocity().add(new Vector(relativeXDiff * firstIntegerValue * velocityMultiplier, integers.get(1).getValue(eventInfo) * velocityMultiplier, relativeZDiff * firstIntegerValue * velocityMultiplier)));			
		}
		else
		{
			double multiplier = Math.abs(firstEntity.getLocation().getX() - secondEntity.getLocation().getX()) + Math.abs(firstEntity.getLocation().getY() - secondEntity.getLocation().getY()) + Math.abs(firstEntity.getLocation().getZ() - secondEntity.getLocation().getZ());
			multiplier *= multiplier;
			multiplier = firstIntegerValue / multiplier * velocityMultiplier;
			
			int relativeYDiff = (int)(firstEntity.getLocation().getY() - secondEntity.getLocation().getY());
			
			firstEntity.setVelocity(firstEntity.getVelocity().add(new Vector((relativeXDiff * relativeXDiff) * multiplier, (relativeYDiff * relativeYDiff) * multiplier, (relativeZDiff * relativeZDiff) * multiplier)));//TODO Use some trig here?
		}
	}
	
	public static void register(){ NestedRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.knockback", Pattern.CASE_INSENSITIVE), new RoutineBuilder());}
	
	protected static class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public Knockback getNew(Matcher matcher, Object nestedContent)
		{
			boolean entityMatches = EntityReference.isValid(matcher.group(1));
			if(nestedContent instanceof LinkedHashMap)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack (parameterized): ");
				
				List<DynamicInteger> integers = new ArrayList<DynamicInteger>();
				if(ParameterizedRoutine.getParameters(integers, (LinkedHashMap<String, Object>)nestedContent, "X", "Y") && entityMatches)
					return new Knockback(matcher.group(), integers.get(0), integers.get(1), EntityReference.match(matcher.group(1)));
				else ModDamage.addToLogRecord(OutputPreset.FAILURE, "");
			}
			else
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, "KnockBack: ");
				List<Routine> routines = new ArrayList<Routine>();
				if(RoutineAliaser.parseRoutines(routines, nestedContent) && entityMatches)
					return new Knockback(matcher.group(), routines, EntityReference.match(matcher.group(1)));
			}
			return null;
		}
	}

}
