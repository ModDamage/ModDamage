package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

public enum ComparisonType
{ 
	EQUALS, NOTEQUALS, LESSTHAN, LESSTHANEQUALS, GREATERTHAN, GREATERTHANEQUALS;

	public static ComparisonType matchType(String key)
	{
		for(ComparisonType type : ComparisonType.values())
			if(key.equalsIgnoreCase(type.name()))
				return type;
		ModDamage.addToConfig(DebugSetting.QUIET, 0, "Invalid comparison \"" + key + "\"", LoadState.FAILURE);
		return null;
	}
	public boolean compare(Integer operand1, Integer operand2)
	{
		if(operand1 == null || operand2 == null) return false;
		switch(this)
		{
			case EQUALS:			return operand1 == operand2;
			case NOTEQUALS:			return operand1 != operand2;
			case LESSTHAN:			return operand1 < operand2;
			case LESSTHANEQUALS:	return operand1 <= operand2;
			case GREATERTHAN:		return operand1 > operand2;
			case GREATERTHANEQUALS:	return operand1 >= operand2;
			default:				return false;
		}
	}
}