package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

public enum LogicalOperation
{ 
	AND("&&"),
	OR("||"),
	NOR("!||"),
	NAND("!&&"),
	XOR("|");

	private final String shorthand;
	private LogicalOperation(String shorthand){ this.shorthand = shorthand;}
	
	public static LogicalOperation matchType(String key)
	{
		for(LogicalOperation operation : LogicalOperation.values())
			if(key.equalsIgnoreCase(operation.name()) || key.equalsIgnoreCase(operation.shorthand))
				return operation;
		return null;
	}
	public boolean operate(boolean operand_1, boolean operand_2)
	{
		switch(this)
		{
			case AND:	return operand_1 && operand_2;
			case OR:	return operand_1 || operand_2;
			case NOR:	return !operand_1 && !operand_2;
			case NAND:	return !operand_1 || !operand_2;
			case XOR:	return operand_1 | operand_2;
			default:	return false;
		}
	}
	
	public String getShortHand(){ return this.shorthand;}
}