package com.ModDamage.Variables.Number;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataParser;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Utils;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.NumberExp;

public class NumberOp extends NumberExp<Number>
{	
	public static enum Operator
	{
		ADD("+", 1)
		{
			@Override
			Number operate(int operand_1, int operand_2) { return operand_1 + operand_2; }
			@Override
			Number operateDouble(double operand_1, double operand_2) { return operand_1 + operand_2; }
		},
		SUBTRACT("\\-", 1)
		{
			@Override
			Number operate(int operand_1, int operand_2) { return operand_1 - operand_2; }
			@Override
			Number operateDouble(double operand_1, double operand_2) { return operand_1 - operand_2; }
		},
		MULTIPLY("*", 2)
		{
			@Override
			Number operate(int operand_1, int operand_2) { return operand_1 * operand_2; }
			@Override
			Number operateDouble(double operand_1, double operand_2) { return operand_1 * operand_2; }
		},
		DIVIDE("/", 2)
		{
			@Override
			Number operate(int operand_1, int operand_2)
			{
				try
				{
					return operand_1 / (double)operand_2;
				}
				catch (ArithmeticException a)
				{
					if (operand_1 > 0) return Integer.MAX_VALUE;
					if (operand_1 < 0) return Integer.MIN_VALUE;
					return 0;
				}
			}
			@Override
			Number operateDouble(double operand_1, double operand_2)
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
			Number operate(int operand_1, int operand_2) { return (int)Math.pow(operand_1, operand_2); }
			@Override
			Number operateDouble(double operand_1, double operand_2) { return (int)Math.pow(operand_1, operand_2); }
		},
		MODULUS("%", 2)
		{
			@Override
			Number operate(int operand_1, int operand_2) { return operand_1 % operand_2; }
			@Override
			Number operateDouble(double operand_1, double operand_2) { return operand_1 % operand_2; }
		};
		
		public final String operatorRegex;
		public final int precedence;
		private Operator(String operatorRegex, int precedence)
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
		
		abstract Number operate(int operand_1, int operand_2);
		abstract Number operateDouble(double operand_1, double operand_2);
	}
	
	public static void register()
	{
		DataProvider.register(Number.class, Number.class, Operator.operatorPattern, new IDataParser<Number, Number>()
				{
					@Override
					public IDataProvider<Number> parse(EventInfo info, IDataProvider<Number> leftDP, Matcher m, StringMatcher sm)
					{
						Operator operator = Operator.operatorMap.get(m.group(1));
						
						IDataProvider<Number> rightDP = DataProvider.parse(info, Number.class, sm.spawn());
						if (rightDP == null)
						{
							ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+sm.string+"\"");
							return null;
						}
						
						NumberOp ioi;
						// precedence calculations here
						if (rightDP instanceof NumberOp && ((NumberOp)rightDP).operator.precedence >= operator.precedence)
						{
							NumberOp r = (NumberOp) rightDP;
							r.startDP = new NumberOp(leftDP, operator, r.startDP);
							ioi = r;
						}
						else
						{
							ioi = new NumberOp(leftDP, operator, rightDP);
						}
						
						sm.accept();
						
						return ioi;
					}
				});
	}
	
	private final Operator operator;
	private final IDataProvider<Number> rightDP;
	public NumberOp(IDataProvider<Number> leftDP, Operator operator, IDataProvider<Number> rightDP)
	{
		super(Number.class, leftDP);
		this.operator = operator;
		this.rightDP = rightDP;
	}

	@Override
	public Number myGet(Number left, EventData data) throws BailException
	{
		Number right = rightDP.get(data);
		if (left == null) left = 0;
		if (right == null) right = 0;
		
		if (Utils.isFloating(left) || Utils.isFloating(right))
			return operator.operateDouble(left.doubleValue(), right.doubleValue());
		else
			return operator.operate(left.intValue(), right.intValue());
	}
	
	@Override
	public String toString()
	{
		return "(" + startDP + " " + operator.operatorRegex.replace("\\", "") + " " + rightDP + ")";
	}
}
