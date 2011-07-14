package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class EntitySwitchCalculation<InfoType> extends SwitchCalculation<InfoType>
{
	protected final boolean forAttacker;
	public EntitySwitchCalculation(boolean forAttacker, LinkedHashMap<InfoType, List<ModDamageCalculation>> switchStatements) 
	{
		super(switchStatements);
		this.forAttacker = forAttacker;
	}
}
