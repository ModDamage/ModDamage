package com.ModDamage.Variables.Int;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;
import com.ModDamage.Expressions.IntegerExp;

public class IntegerOpInt extends IntegerExp<Integer>
{	
	public static enum Operator
	{
		ADD("+", 1)
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 + operand_2;
			}
		},
		SUBTRACT("\\-", 1)
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 - operand_2;
			}
		},
		MULTIPLY("*", 2)
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 * operand_2;
			}
		},
		DIVIDE("/", 2)
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				try
				{
					return operand_1 / operand_2;
				}
				catch (ArithmeticException a)
				{
					if (operand_1 > 0) return Integer.MAX_VALUE;
					if (operand_1 < 0) return Integer.MIN_VALUE;
					return 0;
				}
			}
		},
		EXPONENTIATE("^", 3)
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return (int)Math.pow(operand_1, operand_2);
			}
		},
		MODULUS("%", 2)
		{
			@Override
			int operate(int operand_1, int operand_2)
			{
				return operand_1 % operand_2;
			}
		};
		
		public final String operatorRegex;
		public final Integer precedence;
		private Operator(String operatorRegex, Integer precedence)
		{
			this.operatorRegex = operatorRegex;
			this.precedence = precedence;
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
		DataProvider.register(Integer.class, Integer.class, Operator.operatorPattern, new IDataParser<Integer, Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<Integer> leftDP, Matcher m, StringMatcher sm)
					{
						Operator operator = Operator.operatorMap.get(m.group(1));
						
						IDataProvider<Integer> rightDP = DataProvider.parse(info, Integer.class, sm.spawn());
						if (rightDP == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+sm.string+"\"");
							return null;
						}
						
						IntegerOpInt ioi;
						// precedence calculations here
						if (rightDP instanceof IntegerOpInt && ((IntegerOpInt)rightDP).operator.precedence >= operator.precedence)
						{
							IntegerOpInt r = (IntegerOpInt) rightDP;
							r.startDP = new IntegerOpInt(leftDP, operator, r.startDP);
							ioi = r;
						}
						else
						{
							ioi = new IntegerOpInt(leftDP, operator, rightDP);
						}
						
						sm.accept();
						
						return ioi;
					}
				});
	}
	
	private final Operator operator;
	private final IDataProvider<Integer> rightDP;
	protected IntegerOpInt(IDataProvider<Integer> leftDP, Operator operator, IDataProvider<Integer> rightDP)
	{
		super(Integer.class, leftDP);
		this.operator = operator;
		this.rightDP = rightDP;
	}

	@Override
	public Integer myGet(Integer left, EventData data) throws BailException
	{
		return operator.operate(left, rightDP.get(data));
	}
	
	@Override
	public String toString()
	{
		return "(" + startDP + " " + operator.operatorRegex.replace("\\", "") + " " + rightDP + ")";
	}
}
