package com.ModDamage.Expressions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;

public class IntegerOpExp extends IntegerExp
{	
	public static enum Operator
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
		
		public final String operatorRegex;
		private Operator(String operatorRegex)
		{
			this.operatorRegex = operatorRegex;
		}
		
		public static final Pattern operatorPattern;
		public static final Map<String, Operator> operatorMap = new HashMap<String, Operator>();
		static
		{
			String s = "";
			for (Operator op : Operator.values())
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
		IntegerExp.register(
				Pattern.compile("\\("),
				new DynamicIntegerBuilder()
				{
					@Override
					public IntegerExp getNewFromFront(Matcher m, StringMatcher sm, EventInfo info)
					{
						IntegerExp left = IntegerExp.getIntegerFromFront(sm.spawn(), info);
						if (left == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+sm.string+"\"");
							return null;
						}
						
						Matcher matcher = sm.matchFront(Operator.operatorPattern);
						if (matcher == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Couldn't match operator: \""+sm.string+"\"");
							return null;
						}
						
						IntegerExp right = IntegerExp.getIntegerFromFront(sm.spawn(), info);
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
						
						return sm.acceptIf(new IntegerOpExp(left, Operator.operatorMap.get(matcher.group(1)), right));
					}
				});
	}
	
	private final IntegerExp left;
	private final Operator operator;
	private final IntegerExp right;
	protected IntegerOpExp(IntegerExp left, Operator operator, IntegerExp right)
	{
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override
	protected int myGetValue(EventData data) throws BailException
	{
		return operator.operate(left.getValue(data), right.getValue(data));
	}
	
	@Override
	public String toString()
	{
		return "(" + left + " " + operator.operatorRegex.replace("\\", "") + " " + right + ")";
	}
}
