package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

public enum ComparisonType
{ 
	EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_EQUALS, GREATER_THAN, GREATER_THAN_EQUALS;

	public static ComparisonType matchType(String string)
	{
		if(string.equalsIgnoreCase("EQUALS")) return EQUALS;
		else if(string.equalsIgnoreCase("NOT_EQUALS")) return NOT_EQUALS;
		else if(string.equalsIgnoreCase("LESS_THAN")) return LESS_THAN;
		else if(string.equalsIgnoreCase("LESS_THAN_EQUALS")) return LESS_THAN_EQUALS;
		else if(string.equalsIgnoreCase("GREATER_THAN")) return GREATER_THAN;
		else if(string.equalsIgnoreCase("GREATER_THAN_EQUALS")) return GREATER_THAN_EQUALS;
		return null;
	}
}