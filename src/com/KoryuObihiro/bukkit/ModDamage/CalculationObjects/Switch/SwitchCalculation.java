package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Switch;

import java.util.HashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

abstract public class SwitchCalculation implements ModDamageCalculation 
{
	protected HashMap<Object, List<ModDamageCalculation>> switchLabels;
}
