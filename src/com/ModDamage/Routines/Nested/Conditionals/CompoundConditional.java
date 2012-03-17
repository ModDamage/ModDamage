package com.ModDamage.Routines.Nested.Conditionals;

import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;

public class CompoundConditional extends Conditional
{
	public enum LogicalOperator
	{
		AND
		{
			public boolean operate(EventData data, Conditional operand_1, Conditional operand_2) throws BailException
			{
				return operand_1.evaluate(data) && operand_2.evaluate(data);
			}
		},
		OR
		{
			public boolean operate(EventData data, Conditional operand_1, Conditional operand_2) throws BailException
			{
				return operand_1.evaluate(data) || operand_2.evaluate(data);
			}
		},
		XOR
		{
			public boolean operate(EventData data, Conditional operand_1, Conditional operand_2) throws BailException
			{
				return operand_1.evaluate(data) ^ operand_2.evaluate(data);
			}
		},
		NAND
		{
			public boolean operate(EventData data, Conditional operand_1, Conditional operand_2) throws BailException
			{
				return !(operand_1.evaluate(data) && operand_2.evaluate(data));
			}
		},
		NOR
		{
			public boolean operate(EventData data, Conditional operand_1, Conditional operand_2) throws BailException
			{
				return !(operand_1.evaluate(data) && operand_2.evaluate(data));
			}
		},
		XNOR
		{
			public boolean operate(EventData data, Conditional operand_1, Conditional operand_2) throws BailException
			{
				return !(operand_1.evaluate(data) ^ operand_2.evaluate(data));
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
		
		abstract public boolean operate(EventData data, Conditional operand_1, Conditional operand_2) throws BailException;
	}
	
	
	private final Conditional left;
	private final LogicalOperator operator;
	private final Conditional right;
	public CompoundConditional(String configString, Conditional left, LogicalOperator operator, Conditional right)
	{
		super(configString);
		this.left = left;
		this.operator = operator;
		this.right = right;
	}
	
	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return operator.operate(data, left, right);
	}
}
