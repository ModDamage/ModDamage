package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.SingleValueAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.NestedConditionalStatement;

public class ConditionAliaser extends SingleValueAliaser<ConditionalStatement> 
{
	public ConditionAliaser() {super(AliasManager.Condition.name());}

	@Override
	protected ConditionalStatement matchNonAlias(String key){ return NestedConditionalStatement.getNew("(" + key + ")");}

	@Override
	protected String getObjectName(ConditionalStatement statement){ return "\"" + statement.toString() + "\"";}
}
