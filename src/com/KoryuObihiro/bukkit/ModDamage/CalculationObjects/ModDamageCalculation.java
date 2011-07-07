package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class ModDamageCalculation
{
	static List<Class <? extends ModDamageCalculation>> registeredCalculations = new ArrayList<Class <? extends ModDamageCalculation>>();
	public static List<ModDamageCalculation> parseStrings(List<Object> commandStrings, boolean forSpawn)
	{
		//FIXME
		List<ModDamageCalculation> calculations = new ArrayList<ModDamageCalculation>();
		for(Object calculationString : commandStrings)	
		{
			ModDamageCalculation calculation = null;
			
			if(calculationString instanceof LinkedHashMap)
				calculation = parseNestableCalculation((LinkedHashMap<String, List<Object>>)calculationString, forSpawn);
			else if(calculationString instanceof String)
				calculation = parseBaseCalculation((String)calculationString, forSpawn);
			
			if(calculation != null) calculations.add(calculation);
			else return new ArrayList<ModDamageCalculation>();
		}
		return calculations;
	}
	public static boolean registerCalculation() 
	{
		return false;
	}
	abstract public void calculate(DamageEventInfo eventInfo);
	abstract public void calculate(SpawnEventInfo eventInfo);
}