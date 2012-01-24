package com.ModDamage.Backend.Aliasing;

import com.ModDamage.Backend.Aliasing.Aliaser.SingleValueAliaser;
import com.ModDamage.Routines.Nested.Conditionals.Conditional;

public class ConditionAliaser extends SingleValueAliaser<Conditional> 
{
	public static ConditionAliaser aliaser = new ConditionAliaser();
	public static Conditional match(String string) { return aliaser.matchAlias(string); }
	
	public ConditionAliaser() {super(AliasManager.Condition.name());}

	@Override
	protected Conditional matchNonAlias(String key){ return Conditional.getNew(key);}

	@Override
	protected String getObjectName(Conditional statement){ return "\"" + statement.toString() + "\"";}
}
