package com.ModDamage.Routines.Nested.Calculation;

import com.ModDamage.Backend.EntityReference;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Nested.CalculationRoutine;

abstract public class EntityCalculationRoutine extends CalculationRoutine
{
	protected final EntityReference entityReference;
	public EntityCalculationRoutine(String configString, EntityReference entityReference, DynamicInteger match)
	{
		super(configString, match);
		this.entityReference = entityReference;
	}
}