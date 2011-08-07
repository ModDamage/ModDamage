package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

public enum ComparisonType
{ 
	EQUALS, NOTEQUALS, LESSTHAN, LESSTHANEQUALS, GREATERTHAN, GREATERTHANEQUALS;

	public static ComparisonType matchType(String key)
	{
		for(ComparisonType type : ComparisonType.values())
			if(key.equalsIgnoreCase(type.name()))
				return type;
		return null;
	}
	public boolean compare(int operand1, int operand2)
	{
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