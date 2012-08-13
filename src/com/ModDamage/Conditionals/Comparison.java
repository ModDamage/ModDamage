package com.ModDamage.Conditionals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataProvider;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.EventInfo.IDataProvider;

public class Comparison extends Conditional<Integer>
{
	private enum ComparisonType
	{ 
		LESSTHAN("<", "<")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 < operand2; }
		},
		LESSTHANEQUALS("<=", "<=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 <= operand2; }
		},
		GREATERTHAN(">", ">")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 > operand2; }
		},
		GREATERTHANEQUALS(">=", ">=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 >= operand2; }
		};
		
		//public final String opRegex;
		public final String[] operators;
		
		private ComparisonType(String opRegex, String... operators)
		{
			//this.opRegex = opRegex;
			this.operators = operators;
		}
		
		abstract public boolean compare(int operand1, int operand2);
		
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
	protected final IDataProvider<Integer> rightDP;
	protected final ComparisonType comparisonType;
	
	protected Comparison(IDataProvider<Integer> left, ComparisonType comparisonType, IDataProvider<Integer> right)
	{
		super(Integer.class, left);
		this.comparisonType = comparisonType;
		this.rightDP = right;
	}

	@Override
	public Boolean get(Integer left, EventData data) throws BailException
	{
		return comparisonType.compare(left, rightDP.get(data));
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
		DataProvider.register(Boolean.class, Integer.class, operatorPattern, new IDataParser<Boolean, Integer>()
			{
				@Override
				public IDataProvider<Boolean> parse(EventInfo info, Class<?> want, IDataProvider<Integer> leftDP, Matcher m, StringMatcher sm)
				{
					ComparisonType comparisonType = ComparisonType.nameMap.get(m.group(1));
					
					IDataProvider<Integer> right = DataProvider.parse(info, Integer.class, sm.spawn());
					
					if(comparisonType == null)
						return null;
					
					sm.accept();
					return new Comparison(leftDP, comparisonType, right);
				}
			});
	}
}
