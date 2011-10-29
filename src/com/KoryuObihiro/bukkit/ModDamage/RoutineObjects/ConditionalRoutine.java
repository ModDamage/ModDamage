package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.ParentheticalParser;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Chance;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.Comparison;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBiome;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityBlockStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityStatus;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTagged;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityTypeEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityWearing;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EntityWielding;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventHasRangedElement;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.EventWorldEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.ServerOnlineMode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.WorldEnvironment;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions.PlayerGroupEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Permissions.PlayerPermissionEvaluation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Regions.EntityRegion;

public class ConditionalRoutine extends NestedRoutine
{
	private static HashMap<Pattern, Method> registeredConditionalStatements = new HashMap<Pattern, Method>();
	
	public static String conditionalStatementPart = "!?(.*|\\(.*\\))";

	public static final Pattern conditionalPattern = Pattern.compile("(if|if_not)\\s+(.*)", Pattern.CASE_INSENSITIVE);

	protected final boolean inverted;
	protected final List<ConditionalStatement> statements;
	protected final List<LogicalOperator> operators;
	protected final List<Routine> routines;
	public ConditionalRoutine(String configString, boolean inverted, List<ConditionalStatement> statements, List<LogicalOperator> operators, List<Routine> routines)
	{
		super(configString);
		this.inverted = inverted;
		this.statements = statements;
		this.operators = operators;
		this.routines = routines;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		boolean result = false;
		for(int i = 0; i < statements.size(); i++)
			 result = operators.get(i).operate(eventInfo, result, statements.get(i));
		if(result ^ inverted)
			for(Routine routine : routines)
				routine.run(eventInfo);
	}
	
	public static ConditionalRoutine getNew(String string, Object nestedContent)
	{
		if(string != null && nestedContent != null)
		{
			ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
			ModDamage.addToLogRecord(DebugSetting.NORMAL, "Conditional: \"" + string + "\"", LoadState.SUCCESS);
			
			List<ConditionalStatement> statements = new ArrayList<ConditionalStatement>();
			List<LogicalOperator> operations = new ArrayList<LogicalOperator>();
			operations.add(LogicalOperator.OR);
			
			Matcher matcher = conditionalPattern.matcher(string);
			matcher.matches();
			try
			{
				boolean couldReadStatements = ParentheticalParser.tokenize(matcher.group(2), conditionalStatementPart, LogicalOperator.logicalOperationPart, ConditionalRoutine.class.getMethod("getNewTerm", String.class), LogicalOperator.class.getMethod("match", String.class), statements, operations);
				
				ModDamage.indentation++;
				LoadState[] stateMachine = { LoadState.SUCCESS };
				List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
				ModDamage.indentation--;
					
				if(couldReadStatements && stateMachine[0].equals(LoadState.SUCCESS))
				{
					ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Conditional \"" + string + "\"", LoadState.SUCCESS);
					ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
					return new ConditionalRoutine(string, !matcher.group(1).equalsIgnoreCase("if"), statements, operations, routines);
				}
			}
			catch (Exception e){ e.printStackTrace();}
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid Conditional \"" + string + "\"", LoadState.FAILURE);
			ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
		}
		return null;
	}
	
	public static ConditionalStatement getNewTerm(String string)
	{
		if(string != null)
			for(Pattern pattern : registeredConditionalStatements.keySet())
			{
				Matcher matcher = pattern.matcher(string);
				if(matcher.matches())
					try
					{
						return (ConditionalStatement)registeredConditionalStatements.get(pattern).invoke(null, matcher);
					}
					catch (Exception e){ e.printStackTrace();}
			}
		return null;
	}
	
	public enum LogicalOperator
	{
		AND
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 && (operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		OR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 || (operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		NAND
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return !operand_1 || !(operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		NOR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return !operand_1 && !(operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		XNOR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 == (operand_2.condition(eventInfo) ^ operand_2.inverted);
			}
		},
		XOR
		{
			public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2)
			{
				return operand_1 ^ operand_2.condition(eventInfo);
			}
		};

		public static final String logicalOperationPart;
		static
		{
			String temp = "(";
			for(LogicalOperator operation : LogicalOperator.values())
				temp += operation.name() + "|";
			logicalOperationPart = temp.substring(0, temp.length() - 1) + ")";
		}
		
		public static LogicalOperator match(String key)
		{
			if(key != null)
			{
				for(LogicalOperator operation : LogicalOperator.values())
					if(key.equalsIgnoreCase(operation.name()))
						return operation;	
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid comparison operator \"" + key + "\"", LoadState.FAILURE);		
			}
			return null;
		}
		
		abstract public boolean operate(TargetEventInfo eventInfo, boolean operand_1, ConditionalStatement operand_2);
	}
	
	public static void register()
	{
		registeredConditionalStatements.clear();
		NestedRoutine.registerNested(ConditionalRoutine.class, conditionalPattern);
		registeredConditionalStatements.clear();
		NestedConditionalStatement.register();
		Chance.register();
		Comparison.register();
		//Entity
		EntityBiome.register();
		EntityBlockStatus.register();
		EntityRegion.register();
		EntityStatus.register();
		EntityTagged.register();
		EntityTypeEvaluation.register();
		EntityWearing.register();
		EntityWielding.register();
		EventWorldEvaluation.register();
		PlayerGroupEvaluation.register();
		PlayerPermissionEvaluation.register();
		//Event
		EventHasRangedElement.register();
		EventWorldEvaluation.register();
		//World
		WorldEnvironment.register();
		//Server
		ServerOnlineMode.register();
	}
	
	public static void registerConditionalStatement(Class<? extends ConditionalStatement> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", Matcher.class);
			if(method != null)
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (Matcher)null);
				Routine.register(ConditionalRoutine.registeredConditionalStatements, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch(IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch(IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch(InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}
}
