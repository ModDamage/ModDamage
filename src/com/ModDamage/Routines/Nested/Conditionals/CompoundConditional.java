package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Utils;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Routines.Nested.Conditional;

public class CompoundConditional extends Conditional
{
	public enum LogicalOperator
	{
		AND
		{
			public boolean operate(TargetEventInfo eventInfo, Conditional operand_1, Conditional operand_2)
			{
				return operand_1.evaluate(eventInfo) && operand_2.evaluate(eventInfo);
			}
		},
		OR
		{
			public boolean operate(TargetEventInfo eventInfo, Conditional operand_1, Conditional operand_2)
			{
				return operand_1.evaluate(eventInfo) || operand_2.evaluate(eventInfo);
			}
		},
		XOR
		{
			public boolean operate(TargetEventInfo eventInfo, Conditional operand_1, Conditional operand_2)
			{
				return operand_1.evaluate(eventInfo) ^ operand_2.evaluate(eventInfo);
			}
		},
		NAND
		{
			public boolean operate(TargetEventInfo eventInfo, Conditional operand_1, Conditional operand_2)
			{
				return !(operand_1.evaluate(eventInfo) && operand_2.evaluate(eventInfo));
			}
		},
		NOR
		{
			public boolean operate(TargetEventInfo eventInfo, Conditional operand_1, Conditional operand_2)
			{
				return !(operand_1.evaluate(eventInfo) && operand_2.evaluate(eventInfo));
			}
		},
		XNOR
		{
			public boolean operate(TargetEventInfo eventInfo, Conditional operand_1, Conditional operand_2)
			{
				return !(operand_1.evaluate(eventInfo) ^ operand_2.evaluate(eventInfo));
			}
		};

		public static final Pattern pattern = Pattern.compile("\\s*(" + Utils.joinBy("|", LogicalOperator.values()) + ")\\s*", Pattern.CASE_INSENSITIVE);
		
		
		public static LogicalOperator match(String key)
		{
			try
			{
				return LogicalOperator.valueOf(key.toUpperCase());	
			}
			catch (IllegalArgumentException e) {}
			catch (NullPointerException e) {}
			
			ModDamage.addToLogRecord(OutputPreset.FAILURE, "Invalid comparison operator \"" + key + "\"");
			return null;
		}
		
		abstract public boolean operate(TargetEventInfo eventInfo, Conditional operand_1, Conditional operand_2);
	}
	
	
	final Conditional left;
	final LogicalOperator operator;
	final Conditional right;
	public CompoundConditional(Conditional left, LogicalOperator operator, Conditional right)
	{
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	
	@Override
	public boolean evaluate(TargetEventInfo eventInfo)
	{
		return operator.operate(eventInfo, left, right);
	}
}
