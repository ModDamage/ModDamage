package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Switch;

import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class WorldSwitch<InfoType> extends SwitchCalculation<InfoType>
{	
	public WorldSwitch(LinkedHashMap<InfoType, List<ModDamageCalculation>> switchStatements){ super(switchStatements);}
}
