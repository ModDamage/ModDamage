package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntitySpawn extends EntityCalculationRoutine
{
	final CreatureType creatureType;
	public EntitySpawn(String configString, EntityReference entityReference, CreatureType creatureType, DynamicInteger match)
	{
		super(configString, entityReference, match);
		this.creatureType = creatureType;
	}

	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input) 
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if(input > 0)
			for(int i = 0; i < input; i++)
				entity.getLocation().getWorld().spawnCreature(entity.getLocation(), creatureType);//TODO 0.9.7 - What if I try to spawn a Wolf_Angry? :<
	}
	
	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(\\w+)effect\\.spawn\\.(\\w+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public EntitySpawn getNew(Matcher matcher, DynamicInteger match)
		{
			ModDamageElement element = ModDamageElement.matchElement(matcher.group(2));
			CreatureType creatureType = (element != null)?element.getCreatureType():null;
			if(element != null && creatureType != null && EntityReference.isValid(matcher.group(1)))
				return new EntitySpawn(matcher.group(), EntityReference.match(matcher.group(1)), creatureType, match);
			return null;
		}
	}
}
