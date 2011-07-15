package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;

abstract public class ServerComparison extends ServerConditionalStatement<Integer>
{
	final protected ComparisonType comparisonType;
	ServerComparison(boolean inverted, int value, ComparisonType comparisonType)
	{
		super(inverted, value);
		this.comparisonType = comparisonType;
	}

	@Override
	public boolean condition(DamageEventInfo eventInfo){ return comparisonType.compare(getRelevantInfo(eventInfo), value);}
	
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return comparisonType.compare(getRelevantInfo(eventInfo), value);}

	abstract protected int getRelevantInfo(SpawnEventInfo eventInfo);

	abstract protected int getRelevantInfo(DamageEventInfo eventInfo);
}
