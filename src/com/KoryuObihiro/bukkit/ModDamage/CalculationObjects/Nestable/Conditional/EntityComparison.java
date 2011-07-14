package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ComparisonType;

abstract public class EntityComparison extends EntityConditionalStatement<Integer>
{
	final protected ComparisonType comparisonType;
	EntityComparison(boolean inverted, boolean forAttacker, int value, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, value);
		this.comparisonType = comparisonType;
	}

	@Override
	public boolean condition(DamageEventInfo eventInfo){ return comparisonType.compare(getRelevantInfo(eventInfo), value);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return comparisonType.compare(getRelevantInfo(eventInfo), value);}
}
