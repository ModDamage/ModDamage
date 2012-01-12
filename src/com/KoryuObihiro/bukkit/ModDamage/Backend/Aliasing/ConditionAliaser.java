package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.SingleValueAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class ConditionAliaser extends SingleValueAliaser<Conditional> 
{
	public ConditionAliaser() {super(AliasManager.Condition.name());}

	@Override
	protected Conditional matchNonAlias(String key){ return Conditional.getNew(key);}

	@Override
	protected String getObjectName(Conditional statement){ return "\"" + statement.toString() + "\"";}
}
