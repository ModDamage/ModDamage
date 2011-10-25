package com.KoryuObihiro.bukkit.ModDamage.Backend.Matching;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DynamicCalculatedInteger extends DynamicInteger
{	
	final List<DynamicInteger> integers;
	final List<ArithmeticOperator> operators;
	protected DynamicCalculatedInteger(List<DynamicInteger> integers, List<ArithmeticOperator> operators, boolean isNegative)
	{
		super(isNegative, false);
		this.integers = integers;
		this.operators = operators;
	}

	public static enum ArithmeticOperator
	{
		ADD("\\+")
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
		MULTIPLY("\\*")
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
		EXPONENTIATE("\\^")
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return (int)Math.pow(operand_1, operand_2);
			}
		};
		
		final public String operatorRegex;
		final protected String operatorString;
		private ArithmeticOperator(String operatorRegex)
		{
			this.operatorRegex = operatorRegex;
			this.operatorString = operatorRegex.replace("\\", "");
		}
		
		public static final String arithmeticCharacters;
		static
		{
			String temp = "[";
			for(ArithmeticOperator operator : ArithmeticOperator.values())
				temp += operator.operatorRegex + "|";
			temp = temp.substring(0, temp.length()) + "]";
			arithmeticCharacters = temp;
		}
		
		abstract int operate(int operand_1, int operand_2);
		
		public static ArithmeticOperator match(String string)//TODO Make more efficient?
		{
			if(string != null)
				for(ArithmeticOperator operator : ArithmeticOperator.values())
					if(Pattern.compile(operator.operatorRegex, Pattern.CASE_INSENSITIVE).matcher(string).matches())
						return operator;
			return null;
		}
	}
	
	@Override
	public Integer getValue(TargetEventInfo eventInfo)
	{
		int lastValue = integers.get(0).getValue(eventInfo);
		for(int i = 1; i < integers.size(); i++)
			lastValue = operators.get(i - 1).operate(lastValue, integers.get(i).getValue(eventInfo));
		return (isNegative?-1:1) * lastValue;
	}
	
	public static DynamicCalculatedInteger getNew(String string, boolean isNegative)
	{
		List<DynamicInteger> integers = new ArrayList<DynamicInteger>();
		integers.add(DynamicInteger.getNew("0"));
		List<ArithmeticOperator> operators = new ArrayList<ArithmeticOperator>();
		operators.add(ArithmeticOperator.ADD);
		
		try
		{
			if(ParentheticalParser.tokenize(string, dynamicIntegerPart, ArithmeticOperator.arithmeticCharacters, DynamicInteger.class.getMethod("getNew", String.class), ArithmeticOperator.class.getMethod("match", String.class), integers, operators))
				return new DynamicCalculatedInteger(integers, operators, isNegative);
		} 
		catch (SecurityException e){ e.printStackTrace();}
		catch (NoSuchMethodException e){ e.printStackTrace();}
		return null;
	}
	
	@Override
	public String toString()
	{
		String displayString = (isNegative?"-":"") + "(" + integers.get(1).toString();
		for(int i = 1; i < operators.size(); i++)
		{
			displayString += " " + operators.get(i).operatorString;
			displayString += " " + integers.get(i + 1).toString();
		}
		displayString += ")";
			
		return displayString;
	}
}
