package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;

public enum LogicalOperation
{ 
	AND, OR, NOR, NAND, XOR;

	public static LogicalOperation matchType(String key)
	{
		for(LogicalOperation operation : LogicalOperation.values())
			if(key.equalsIgnoreCase(operation.name()))
				return operation;	
		ModDamage.addToConfig(DebugSetting.QUIET, 0, "Invalid comparison operator \"" + key + "\"", LoadState.FAILURE);		
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
			default:return false;
		}
	}
}