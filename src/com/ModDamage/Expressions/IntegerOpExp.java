package com.ModDamage.Expressions;

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

public class IntegerOpExp extends IntegerExp<Integer>
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
		DataProvider.register(Integer.class, Integer.class, Pattern.compile("\\("), new IDataParser<Integer>()
				{
					@Override
					public IDataProvider<Integer> parse(EventInfo info, IDataProvider<?> entityDP, Matcher m, StringMatcher sm)
					{
						IDataProvider<Integer> left = DataProvider.parse(info, Integer.class, sm.spawn());
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
						
						IDataProvider<Integer> right = DataProvider.parse(info, Integer.class, sm.spawn());
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
	
	private final Operator operator;
	private final IDataProvider<Integer> rightDP;
	protected IntegerOpExp(IDataProvider<Integer> left, Operator operator, IDataProvider<Integer> right)
	{
		super(Integer.class, left);
		this.operator = operator;
		this.rightDP = right;
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
