package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

public enum ComparisonType
{ 
	EQUALS("=="),
	NOT_EQUALS("!="),
	LESS_THAN("<"),
	LESS_THAN_EQUALS("<="),
	GREATER_THAN(">"),
	GREATER_THAN_EQUALS(">=");

	private final String shorthand;
	private ComparisonType(String shorthand)
	{
		this.shorthand = shorthand;
	}
	public static ComparisonType matchType(String key)
	{
		for(ComparisonType type : ComparisonType.values())
			if(key.equalsIgnoreCase(type.name()) || key.equalsIgnoreCase(type.shorthand))
				return type;
		return null;
	}
	public boolean compare(int operand_1, int operand_2)
	{
		switch(this)
		{
			case EQUALS:				return operand_1 == operand_2;
			case NOT_EQUALS:			return operand_1 != operand_2;
			case LESS_THAN:				return operand_1 < operand_2;
			case LESS_THAN_EQUALS:		return operand_1 <= operand_2;
			case GREATER_THAN:			return operand_1 > operand_2;
			case GREATER_THAN_EQUALS:	return operand_1 >= operand_2;
			default:					return false;
		}
	}
	
	public String getShortHand(){ return this.shorthand;}
}