package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.ConditionalStatement;

public class Comparison extends ConditionalStatement
{	
	protected final DynamicInteger operand1;
	protected final DynamicInteger operand2;
	protected final ComparisonType comparisonType;
	protected Comparison(boolean inverted, DynamicInteger operand1, DynamicInteger operand2, ComparisonType comparisonType)
	{
		super(inverted);
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.comparisonType = comparisonType;
	}

	@Override
	public boolean condition(TargetEventInfo eventInfo) { return comparisonType.compare(operand1.getValue(eventInfo), operand2.getValue(eventInfo));}

	private enum ComparisonType
	{ 
		EQUALS("==")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 == operand2;}
		},
		NOTEQUALS("!=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 != operand2;}
		},
		LESSTHAN("<")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 < operand2;}
		},
		LESSTHANEQUALS("<=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 <= operand2;}
		},
		GREATERTHAN(">")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 > operand2;}
		},
		GREATERTHANEQUALS(">=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 >= operand2;}
		};
		
		public final String operator;
		
		private ComparisonType(String op)
		{
			operator = op;
		}
		
		abstract public boolean compare(int operand1, int operand2);
	}

	protected static String comparisonPart;
	protected static String operatorPart;
	protected static Map<String, ComparisonType> nameMap;
	static
	{
		nameMap = new HashMap<String, ComparisonType>();
		
		comparisonPart = "";
		operatorPart = "";
		for(ComparisonType comparisonType : ComparisonType.values())
		{
			comparisonPart += comparisonType.name() + "|";
			nameMap.put(comparisonType.name(), comparisonType);
			operatorPart += comparisonType.operator + "|";
			nameMap.put(comparisonType.operator, comparisonType);
		}
		comparisonPart = comparisonPart.substring(0, comparisonPart.length() - 1);
		operatorPart = operatorPart.substring(0, operatorPart.length() - 1);
		
		nameMap = Collections.unmodifiableMap(nameMap);
	}
	public static void register()
	{
		ConditionalRoutine.registerConditionalStatement(Pattern.compile("(!?)([\\w.]*)(?:\\.(" + comparisonPart + ")\\.|\\s*(" + operatorPart + ")\\s*)([\\w.]*)", Pattern.CASE_INSENSITIVE), new StatementBuilder());
	}
	
	protected static class StatementBuilder extends ConditionalStatement.StatementBuilder
	{	
		@Override
		public Comparison getNew(Matcher matcher)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(2)), match2 = DynamicInteger.getNew(matcher.group(5));
			String op = matcher.group(3);
			if (op == null) op = matcher.group(4);
			ComparisonType comparisonType = nameMap.get(op.toUpperCase());
			if(comparisonType != null && match1 != null && match2 != null)
				return new Comparison(matcher.group(1).equalsIgnoreCase("!"), match1, match2, comparisonType);
			return null;
		}
	}
}
