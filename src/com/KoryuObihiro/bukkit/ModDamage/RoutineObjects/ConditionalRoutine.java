package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class ConditionalRoutine extends Routine
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	final protected boolean inverted;
	final protected List<Routine> routines;
	final List<ConditionalStatement> statements;
	final List<LogicalOperation> logicalOperations;
	public ConditionalRoutine(boolean inverted, List<ConditionalStatement> statements, List<LogicalOperation> logicalOperations, List<Routine> routines)
	{
		this.inverted = inverted;
		this.statements = statements;
		this.logicalOperations = logicalOperations;
		this.routines = routines;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		boolean result = statements.get(0).condition(eventInfo);
		for(int i = 1; i < statements.size(); i++)
			 result = logicalOperations.get(i - 1).operate(result, statements.get(i).condition(eventInfo));
		if(result | inverted) 
			for(Routine routine : routines)
				routine.run(eventInfo);
	}
	
	public static ConditionalRoutine getNew(Matcher matcher, List<Routine> routines)
	{
		List<ConditionalStatement> statements = new ArrayList<ConditionalStatement>();
		List<LogicalOperation> operations = new ArrayList<LogicalOperation>();
		//start with a programmatic "false ||" ... because of how this routine is executed.
		
		//parse all of the conditionals
		String[] statementStrings = matcher.group(2).split("\\s+");
		for(int i = 0; i <= statementStrings.length; i += 2)
		{
			for(Pattern pattern : registeredStatements.keySet())
			{
				Matcher statementMatcher = pattern.matcher(statementStrings[i]);
				if(statementMatcher.matches())
				{
					Method method = registeredStatements.get(pattern);
					//get next statement
					ConditionalStatement statement = null;
					try 
					{
						statement = (ConditionalStatement)method.invoke(null, statementMatcher);
					}
					catch (Exception e){ e.printStackTrace();}
					
					if(statement == null)
					{
						ModDamage.addToConfig(DebugSetting.QUIET, 0, "Error: bad statement \"" + statementStrings[i] + "\"", LoadState.FAILURE);
						return null;
					}
					//get its relation to the previous statement
					if(i >= 2)
					{
						LogicalOperation operation = LogicalOperation.matchType(statementStrings[i - 1]);
						if(operation == null)
						{
							ModDamage.addToConfig(DebugSetting.QUIET, 0, "Error: bad operator \"" + statementStrings[i - 1] + "\"", LoadState.FAILURE);
							return null;//shouldn't ever happen
						}
						operations.add(operation);
					}
					statements.add(statement);
					break;
				}
			}
		}
		if(!statements.isEmpty())
		{
			statements.add(0, new FalseStatement());
			operations.add(0, LogicalOperation.OR);
			return new ConditionalRoutine(!matcher.group(1).equalsIgnoreCase("if"), statements, operations, routines);
		}
		return null;
	}
	
	public static void registerStatement(ModDamage routineUtility, Class<? extends ConditionalStatement> statementClass, Pattern syntax)
	{
		ModDamage.registerConditional(statementClass, syntax);
	}
	
	private static class FalseStatement extends ConditionalStatement
	{
		FalseStatement(){ super(false);}
		@Override
		public boolean condition(TargetEventInfo eventInfo){ return false;}
	}
}
