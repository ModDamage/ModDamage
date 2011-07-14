package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects;

public enum LogicalOperation
{ 
	AND("&&"),
	OR("||"),
	NOR("!|"),
	NAND("!&"),
	XOR("|");

	private final String symbol;
	private LogicalOperation(String symbol){ this.symbol = symbol;}
	
	public static LogicalOperation matchType(String key)
	{
		for(LogicalOperation operation : LogicalOperation.values())
			if(key.equalsIgnoreCase(operation.name()) || key.equalsIgnoreCase(operation.symbol))
				return operation;
		return null;
	}
	public static boolean operate(LogicalOperation operation, int operand_1, int operand_2)
	{
		switch(operation)
		{
			case AND:	return operand_1 == operand_2;
			case OR:	return operand_1 != operand_2;
			case NOR:	return operand_1 < operand_2;
			case NAND:	return operand_1 <= operand_2;
			case XOR:	return operand_1 > operand_2;
			default:	return false;
		}
	}
}