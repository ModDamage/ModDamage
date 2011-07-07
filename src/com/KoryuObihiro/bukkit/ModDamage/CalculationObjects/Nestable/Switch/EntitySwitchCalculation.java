package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

abstract public class EntitySwitchCalculation<T> extends SwitchCalculation<T>
{
	protected final boolean forAttacker;
	public EntitySwitchCalculation(boolean forAttacker, LinkedHashMap<String, List<Object>> switchStatements) 
	{
		super(switchStatements);
		this.forAttacker = forAttacker;
	}
}
