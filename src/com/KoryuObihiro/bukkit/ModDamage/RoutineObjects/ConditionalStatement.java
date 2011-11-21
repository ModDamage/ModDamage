package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.List;
import java.util.regex.Matcher;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class ConditionalStatement
{
	public enum LogicalOperator
	{
		AND
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 && (operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		OR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 || (operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		NAND
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return !operand_1 || !(operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		NOR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return !operand_1 && !(operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		XNOR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 == (operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		XOR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 ^ operand_2.condition(eventInfo);
			}
		};

		public static final String logicalOperationPart;
		static
		{
			String temp = "(";
			for(LogicalOperator operation : LogicalOperator.values())
				temp += operation.name() + "|";
			logicalOperationPart = temp.substring(0, temp.length() - 1) + ")";
		}
		
		public static LogicalOperator match(String key)
		{
			if(key != null)
			{
				for(LogicalOperator operation : LogicalOperator.values())
					if(key.equalsIgnoreCase(operation.name()))
						return operation;	
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid comparison operator \"" + key + "\"");		
			}
			return null;
		}
		
		abstract public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2);
	}
	protected final boolean inverted;
	protected ConditionalStatement(boolean inverted)
	{
		this.inverted = inverted;
	}
	
	public abstract boolean condition(TargetEventInfo eventInfo);

	abstract protected static class StatementBuilder
	{
		abstract public ConditionalStatement getNew(Matcher matcher);
	}
	
	public static boolean evaluateStatements(TargetEventInfo eventInfo, List<ConditionalStatement> statements, List<LogicalOperator> operators)
	{
		boolean result = false;
		for(int i = 0; i < statements.size(); i++)
			 result = operators.get(i).operate(eventInfo, result, statements.get(i));
		return result;
	}
}
