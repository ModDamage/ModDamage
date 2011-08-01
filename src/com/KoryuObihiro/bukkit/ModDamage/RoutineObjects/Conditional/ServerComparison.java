package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import com.KoryuObihiro.bukkit.ModDamage.Backend.AttackerEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
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
	public boolean condition(TargetEventInfo eventInfo){ return comparisonType.compare(getRelevantInfo(eventInfo), value);}

	abstract protected int getRelevantInfo(TargetEventInfo eventInfo);

	abstract protected int getRelevantInfo(AttackerEventInfo eventInfo);
}
