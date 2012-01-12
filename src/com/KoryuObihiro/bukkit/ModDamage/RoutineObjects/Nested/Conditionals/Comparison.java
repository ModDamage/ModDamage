package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditionals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger.DIResult;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Conditional;

public class Comparison extends Conditional
{
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
	}
	
	public static final Pattern namePattern = Pattern.compile("\\.(" + ComparisonType.comparisonPart + ")\\.", Pattern.CASE_INSENSITIVE);
	public static final Pattern operatorPattern = Pattern.compile("\\s*(>=?|<=?|==|!=)\\s*"); // doing it like above won't match >= correctly
	protected final DynamicInteger operand1;
	protected final DynamicInteger operand2;
	protected final ComparisonType comparisonType;
	protected Comparison(DynamicInteger operand1, ComparisonType comparisonType, DynamicInteger operand2)
	{
		this.operand1 = operand1;
		this.comparisonType = comparisonType;
		this.operand2 = operand2;
	}

	@Override
	public boolean evaluate(TargetEventInfo eventInfo) { return comparisonType.compare(operand1.getValue(eventInfo), operand2.getValue(eventInfo));}

	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.ConditionalBuilder
	{
		@Override
		public CResult getNewFromFront(String string)
		{
			DIResult res1 = DynamicInteger.getIntegerFromFront(string);
			if (res1 == null) return null;
			
			ComparisonType comparisonType;
			
			Matcher matcher = operatorPattern.matcher(res1.rest);
			if (matcher.lookingAt())
			{
				comparisonType = ComparisonType.nameMap.get(matcher.group(1));
			}
			else
			{
				matcher = namePattern.matcher(res1.rest);
				if (matcher.lookingAt())
				{
					comparisonType = ComparisonType.nameMap.get(matcher.group(1).toUpperCase());
					
					ModDamage.addToLogRecord(OutputPreset.WARNING, "The named operator syntax is deprecated. Please use " + comparisonType.operator + "instead of ." + comparisonType.name() + ".");
				}
				else
					return null;
			}
			
			String after = res1.rest.substring(matcher.end());
			
			DIResult res2 = DynamicInteger.getIntegerFromFront(after);
			if (res2 == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \"" + after + "\"");
				return null;
			}
			
			
			if(comparisonType != null)
				return new CResult(new Comparison(res1.integer, comparisonType, res2.integer), res2.rest);
			return null;
		}
	}
}