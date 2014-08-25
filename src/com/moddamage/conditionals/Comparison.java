package com.moddamage.conditionals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.moddamage.StringMatcher;
import com.moddamage.Utils;
import com.moddamage.backend.BailException;
import com.moddamage.eventinfo.EventData;
import com.moddamage.eventinfo.EventInfo;
import com.moddamage.parsing.DataProvider;
import com.moddamage.parsing.IDataParser;
import com.moddamage.parsing.IDataProvider;

public class Comparison extends Conditional<Number>
{
	private enum ComparisonType
	{ 
		LESSTHAN("<", "<")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 < operand2; }
			@Override
			public boolean compareDouble(double operand1, double operand2){ return operand1 < operand2; }
		},
		LESSTHANEQUALS("<=", "<=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 <= operand2; }
			@Override
			public boolean compareDouble(double operand1, double operand2){ return operand1 <= operand2; }
		},
		GREATERTHAN(">", ">")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 > operand2; }
			@Override
			public boolean compareDouble(double operand1, double operand2){ return operand1 > operand2; }
		},
		GREATERTHANEQUALS(">=", ">=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 >= operand2; }
			@Override
			public boolean compareDouble(double operand1, double operand2){ return operand1 >= operand2; }
		};
		
		//public final String opRegex;
		public final String[] operators;
		
		private ComparisonType(String opRegex, String... operators)
		{
			//this.opRegex = opRegex;
			this.operators = operators;
		}
		
		abstract public boolean compare(int operand1, int operand2);
		abstract public boolean compareDouble(double operand1, double operand2);
		
		//protected static String operatorPart;
		protected static Map<String, ComparisonType> nameMap;
		static
		{
			nameMap = new HashMap<String, ComparisonType>();
			
			//operatorPart = "";
			for(ComparisonType comparisonType : ComparisonType.values())
			{
				//operatorPart += comparisonType.opRegex + "|";
				
				nameMap.put(comparisonType.name(), comparisonType);
				for (String op : comparisonType.operators)
					nameMap.put(op, comparisonType);
			}
			//operatorPart = operatorPart.substring(0, operatorPart.length() - 1);
			
			nameMap = Collections.unmodifiableMap(nameMap);
		}
	}
	
	public static final Pattern operatorPattern = Pattern.compile("\\s*(>=?|<=?|==?|!=)\\s*"); // doing it like above won't match >= correctly
	protected final IDataProvider<Number> rightDP;
	protected final ComparisonType comparisonType;
	
	protected Comparison(IDataProvider<Number> left, ComparisonType comparisonType, IDataProvider<Number> right)
	{
		super(Number.class, left);
		this.comparisonType = comparisonType;
		this.rightDP = right;
	}

	@Override
	public Boolean get(Number left, EventData data) throws BailException
	{
		Number right = rightDP.get(data);
		if (left == null || right == null) return false;
		
		if (Utils.isFloating(left) || Utils.isFloating(right))
			return comparisonType.compareDouble(left.doubleValue(), right.doubleValue());
		else
			return comparisonType.compare(left.intValue(), right.intValue());
	}

	@Override
	public Class<Boolean> provides() { return Boolean.class; }
	
	@Override
	public String toString()
	{
		return startDP + comparisonType.operators[0] + rightDP;
	}

	
	public static void register()
	{
		DataProvider.register(Boolean.class, Number.class, operatorPattern, new IDataParser<Boolean, Number>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, IDataProvider<Number> leftDP, Matcher m, StringMatcher sm)
				{
					ComparisonType comparisonType = ComparisonType.nameMap.get(m.group(1));
					
					IDataProvider<Number> right = DataProvider.parse(info, Number.class, sm.spawn());
					
					if(comparisonType == null)
						return null;
					
					sm.accept();
					return new Comparison(leftDP, comparisonType, right);
				}
			});
	}
}
