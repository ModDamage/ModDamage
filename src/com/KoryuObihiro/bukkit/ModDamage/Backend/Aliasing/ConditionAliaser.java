package com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing;

import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.Aliaser.SingleValueAliaser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalStatement;

public class ConditionAliaser extends SingleValueAliaser<ConditionalStatement> 
{
	private static final long serialVersionUID = 7539931612104625797L;

	public ConditionAliaser() {super("Condition");}

	@Override
	protected ConditionalStatement matchNonAlias(String key){ return ConditionalRoutine.getNewTerm("(" + key + ")");}

	@Override
	protected String getObjectName(ConditionalStatement statement){ return "\"" + statement.toString() + "\"";}
}
