package com.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class DynamicCalculatedInteger extends DynamicInteger
{	
	public static enum ArithmeticOperator
	{
		ADD("+")
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 + operand_2;
			}
		},
		SUBTRACT("\\-")
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 - operand_2;
			}
		},
		MULTIPLY("*")
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 * operand_2;
			}
		},
		DIVIDE("/")
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 / operand_2;
			}
		},
		EXPONENTIATE("^")
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return (int)Math.pow(operand_1, operand_2);
			}
		},
		MODULUS("%")
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 % operand_2;
			}
		};
		
		final public String operatorRegex;
		private ArithmeticOperator(String operatorRegex)
		{
			this.operatorRegex = operatorRegex;
		}
		
		public static final Pattern operatorPattern;
		public static final Map<String, ArithmeticOperator> operatorMap = new HashMap<String, ArithmeticOperator>();
		static
		{
			String s = "";
			for (ArithmeticOperator op : ArithmeticOperator.values())
			{
				operatorMap.put(op.operatorRegex.replace("\\", ""), op);
				s += op.operatorRegex;
			}
			operatorPattern = Pattern.compile("\\s*([" + s + "])\\s*");
		}
		
		abstract int operate(int operand_1, int operand_2);
	}
	
	public static void register()
	{
		final Pattern endPattern = Pattern.compile("\\s*\\)");
		DynamicInteger.register(
				Pattern.compile("\\("),
				new DynamicIntegerBuilder()
				{
					@Override
					public DynamicInteger getNewFromFront(Matcher m, StringMatcher sm, EventInfo info)
					{
						DynamicInteger left = DynamicInteger.getIntegerFromFront(sm.spawn(), info);
						if (left == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+sm.string+"\"");
							return null;
						}
						
						Matcher matcher = sm.matchFront(ArithmeticOperator.operatorPattern);
						if (matcher == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Couldn't match operator: \""+sm.string+"\"");
							return null;
						}
						
						DynamicInteger right = DynamicInteger.getIntegerFromFront(sm.spawn(), info);
						if (right == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+sm.string+"\"");
							return null;
						}
						
						if (sm.matchFront(endPattern) == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing close paren: \""+sm.string+"\"");
							return null;
						}
						
						return sm.acceptIf(new DynamicCalculatedInteger(left, ArithmeticOperator.operatorMap.get(matcher.group(1)), right));
					}
				});
	}
	
	final DynamicInteger left;
	final ArithmeticOperator operator;
	final DynamicInteger right;
	protected DynamicCalculatedInteger(DynamicInteger left, ArithmeticOperator operator, DynamicInteger right)
	{
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	public int getValue(EventData data)
	{
		return operator.operate(left.getValue(data), right.getValue(data));
	}
	
	@Override
	public String toString()
	{
		return "(" + left + " " + operator.operatorRegex.replace("\\", "") + " " + right + ")";
	}
}
