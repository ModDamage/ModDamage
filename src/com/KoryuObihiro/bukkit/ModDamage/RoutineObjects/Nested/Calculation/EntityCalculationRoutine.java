package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

abstract public class EntityCalculationRoutine extends CalculationRoutine
{
	protected final EntityReference entityReference;
	public EntityCalculationRoutine(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, match);
		this.entityReference = entityReference;
	}
}