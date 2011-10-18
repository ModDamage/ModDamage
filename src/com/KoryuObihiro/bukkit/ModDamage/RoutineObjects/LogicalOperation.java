package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

public enum LogicalOperation
{
	AND{public boolean operate(boolean operand_1, boolean operand_2){ return operand_1 && operand_2;}},
	OR{public boolean operate(boolean operand_1, boolean operand_2){ return operand_1 || operand_2;}},
	NAND{public boolean operate(boolean operand_1, boolean operand_2){ return !operand_1 || !operand_2;}},
	NOR{public boolean operate(boolean operand_1, boolean operand_2){ return !operand_1 && !operand_2;}},
	XNOR{public boolean operate(boolean operand_1, boolean operand_2){ return operand_1 == operand_2;}},
	XOR{public boolean operate(boolean operand_1, boolean operand_2){ return operand_1 ^ operand_2;}};

	public static final String logicalOperationPart;
	static
	{
		String temp = "(";
		for(LogicalOperation operation : LogicalOperation.values())
			temp += operation.name() + "|";
		logicalOperationPart = temp.substring(0, temp.length() - 1) + ")";
	}
	
	public static LogicalOperation matchType(String key)
	{
		for(LogicalOperation operation : LogicalOperation.values())
			if(key.equalsIgnoreCase(operation.name()))
				return operation;	
		ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid comparison operator \"" + key + "\"", LoadState.FAILURE);		
		return null;
	}
	
	abstract public boolean operate(boolean operand_1, boolean operand_2);
}