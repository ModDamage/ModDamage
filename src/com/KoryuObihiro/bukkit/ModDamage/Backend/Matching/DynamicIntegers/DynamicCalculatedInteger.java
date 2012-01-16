package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicIntegers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

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
					public DIResult getNewFromFront(Matcher m, String rest)
					{
						DIResult leftDir = DynamicInteger.getIntegerFromFront(rest);
						if (leftDir == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+rest+"\"");
							return null;
						}
						
						Matcher matcher = ArithmeticOperator.operatorPattern.matcher(leftDir.rest);
						if (!matcher.lookingAt())
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Couldn't match operator: \""+leftDir.rest+"\"");
							return null;
						}
						
						DIResult rightDir = DynamicInteger.getIntegerFromFront(leftDir.rest.substring(matcher.end()));
						if (rightDir == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+leftDir.rest.substring(matcher.end())+"\"");
							return null;
						}
						
						Matcher endMatcher = endPattern.matcher(rightDir.rest);
						if (!endMatcher.lookingAt())
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Missing close paren: \""+rightDir.rest+"\"");
							return null;
						}
						
						return new DIResult(new DynamicCalculatedInteger(
								leftDir.integer, ArithmeticOperator.operatorMap.get(matcher.group(1)), rightDir.integer), rightDir.rest.substring(endMatcher.end()));
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
	public int getValue(TargetEventInfo eventInfo)
	{
		return operator.operate(left.getValue(eventInfo), right.getValue(eventInfo));
	}
	
	@Override
	public String toString()
	{
		return "(" + left + " " + operator.operatorRegex.replace("\\", "") + " " + right + ")";
	}
}
