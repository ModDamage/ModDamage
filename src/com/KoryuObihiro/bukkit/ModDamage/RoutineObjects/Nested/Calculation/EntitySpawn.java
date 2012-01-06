package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class EntitySpawn extends EntityCalculationRoutine
{
	final ModDamageElement spawnElement;
	public EntitySpawn(String configString, EntityReference entityReference, ModDamageElement spawnElement, DynamicInteger match)
	{
		super(configString, entityReference, match);
		this.spawnElement = spawnElement;
	}

	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input) 
	{
		Entity entity = entityReference.getEntity(eventInfo);
		if (entity == null)
			entity = EntityReference.TARGET.getEntity(eventInfo);
		if(input > 0)
			for(int i = 0; i < input; i++)
				spawnElement.spawnCreature(entity.getLocation());
	}
	
	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(.*)effect\\.spawn\\.(.*)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends CalculationRoutine.CalculationBuilder
	{	
		@Override
		public EntitySpawn getNew(Matcher matcher, DynamicInteger match)
		{
			ModDamageElement element = ModDamageElement.matchElement(matcher.group(2));
			EntityReference reference = EntityReference.match(matcher.group(1));
			if(element != null && element.canSpawnCreature() && reference != null)
				return new EntitySpawn(matcher.group(), reference, element, match);
			return null;
		}
	}
}
