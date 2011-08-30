package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

abstract public class EntityComparison extends EntityConditionalStatement<Integer>
{
	final protected ComparisonType comparisonType;
	EntityComparison(boolean inverted, boolean forAttacker, int value, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, value);
		this.comparisonType = comparisonType;
	}

	@Override
	public boolean condition(TargetEventInfo eventInfo){ return comparisonType.compare(getRelevantInfo(eventInfo), value);}
}
