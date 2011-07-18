package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.LogicalOperation;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

public class ConditionalRoutine extends Routine
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	//For every statement, there must be a relation after it. Merely cap the last one with AND(asdf, true);
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
	public void run(DamageEventInfo eventInfo)
	{
		boolean result = statements.get(0).condition(eventInfo);
		for(int i = 1; i < statements.size(); i++)
			 result = logicalOperations.get(i).operate(result, statements.get(i).condition(eventInfo));
		if(inverted?!result:result) executeNested(eventInfo);
	}
	
	@Override
	public void run(SpawnEventInfo eventInfo)
	{
		boolean result = statements.get(0).condition(eventInfo);
		for(int i = 1; i < statements.size(); i++)
			 result = logicalOperations.get(i).operate(result, statements.get(i).condition(eventInfo));
		if(inverted?!result:result) executeNested(eventInfo);
	}
	

	protected void executeNested(DamageEventInfo eventInfo)
	{
		for(Routine routine : routines)
			routine.run(eventInfo);
	}
	protected void executeNested(SpawnEventInfo eventInfo)
	{
		for(Routine routine : routines)
			routine.run(eventInfo);
	}
	
	public static ConditionalRoutine getNew(Matcher matcher, List<Routine> routines)
	{
		//start with a programmatic "false ||" ... because of how this calculation is executed.
		List<ConditionalStatement> statements = new ArrayList<ConditionalStatement>();
		List<LogicalOperation> operations = new ArrayList<LogicalOperation>();
		statements.add(new FalseStatement());
		operations.add(LogicalOperation.OR);
		
		//parse all of the conditionals
		for(int i = 0; i < matcher.groupCount(); i += 2)
			for(Pattern pattern : registeredStatements.keySet())
			{
				Matcher statementMatcher = pattern.matcher(matcher.group(i));
				if(statementMatcher.matches())
				{
					Method method = registeredStatements.get(pattern);
					//get next statement
					ConditionalStatement statement = null;
					try 
					{
						statement = (ConditionalStatement)method.invoke(null, statementMatcher, routines);
					}
					catch (Exception e){ e.printStackTrace();}
					
					if(statement == null) return null;
					//get its relation to the previous statement
					if(i > 1)
					{
						LogicalOperation operation = LogicalOperation.matchType(matcher.group(i - 1));
						if(operation == null) return null;//shouldn't ever happen
						operations.add(operation);
					}
					statements.add(statement);
				}
			}
		return null;
	}
	
	public static void registerStatement(RoutineUtility routineUtility, Class<? extends ConditionalStatement> statementClass, Pattern syntax)
	{
		routineUtility.registerConditional(statementClass, syntax);
	}
	
	private static class FalseStatement extends ConditionalStatement
	{
		FalseStatement(){ super(false);}
		@Override
		public boolean condition(DamageEventInfo eventInfo){ return false;}
		@Override
		public boolean condition(SpawnEventInfo eventInfo){ return false;}
	}
}
