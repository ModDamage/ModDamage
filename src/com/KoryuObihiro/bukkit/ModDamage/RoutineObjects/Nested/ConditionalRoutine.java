package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

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
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional.LogicalOperation;

public class ConditionalRoutine extends NestedRoutine
{
	public static HashMap<Pattern, Method> registeredConditionalStatements = new HashMap<Pattern, Method>();
	public static final Pattern conditionalPattern = Pattern.compile("(if|if_not)\\s+(" + RoutineAliaser.statementPart + "(?:\\s+([\\*\\w]+)\\s+" +  RoutineAliaser.statementPart + ")*)", Pattern.CASE_INSENSITIVE);
	
	protected final boolean inverted;
	protected final List<ConditionalStatement> statements;
	protected final List<LogicalOperation> LogicalOperations;
	protected final List<Routine> routines;
	public ConditionalRoutine(String configString, boolean inverted, List<ConditionalStatement> statements, List<LogicalOperation> LogicalOperations, List<Routine> routines)
	{
		super(configString);
		this.inverted = inverted;
		this.statements = statements;
		this.LogicalOperations = LogicalOperations;
		this.routines = routines;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		boolean result = statements.get(0).condition(eventInfo);
		for(int i = 1; i < statements.size(); i++)
			 result = LogicalOperations.get(i - 1).operate(result, statements.get(i).condition(eventInfo) ^ statements.get(i).inverted);
		if(result ^ inverted)
			for(Routine routine : routines)
				routine.run(eventInfo);
	}
	
	public static ConditionalRoutine getNew(String string, Object nestedContent)
	{
		if(string != null && nestedContent != null)
		{
			Matcher matcher = conditionalPattern.matcher(string);
			if(matcher.matches())
			{
				ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "Conditional: \"" + string + "\"", LoadState.SUCCESS);
				
				List<ConditionalStatement> statements = new ArrayList<ConditionalStatement>();
				List<LogicalOperation> operations = new ArrayList<LogicalOperation>();
				//start with a programmatic "false ||" ... because of how this routine is executed.
				
				//parse all of the conditionals
				String[] statementStrings = matcher.group(2).split("\\s+");//FIXME 0.9.7 - Change this algorithm so it uses NestedConditionalStatement for parentheses. :D
				for(int i = 0; i <= statementStrings.length; i += 2)
				{
					boolean failFlag = false;
					for(Pattern pattern : registeredConditionalStatements.keySet())
					{
						Matcher statementMatcher = pattern.matcher(statementStrings[i]);
						if(statementMatcher.matches())
						{
							Method method = registeredConditionalStatements.get(pattern);
							//get next statement
							ConditionalStatement statement = null;
							try 
							{
								statement = (ConditionalStatement)method.invoke(null, statementMatcher);
							}
							catch (Exception e){ e.printStackTrace();}
							
							if(statement == null) ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: bad statement \"" + statementStrings[i] + "\"", LoadState.FAILURE);
							//get its relation to the previous statement
							//FIXME Does this not catch stray operators?
							if(i >= 2)
							{
								LogicalOperation operation = LogicalOperation.matchType(statementStrings[i - 1]);
								if(operation == null)
								{
									ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: bad operator \"" + statementStrings[i - 1] + "\"", LoadState.FAILURE);
									failFlag = true;
									break;
								}
								operations.add(operation);
							}
							statements.add(statement);
							break;
						}
					}
					if(failFlag) break;
				}
				if(!statements.isEmpty())
				{
					statements.add(0, new FalseStatement());
					operations.add(0, LogicalOperation.OR);
					ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Conditional \"" + string + "\"\n", LoadState.SUCCESS);

					LoadState[] stateMachine = { LoadState.SUCCESS };
					List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
					if(stateMachine[0].equals(LoadState.SUCCESS))
						return new ConditionalRoutine(string, !matcher.group(1).equalsIgnoreCase("if"), statements, operations, routines);
				}
			}
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid Conditional" + " \"" + string + "\"", LoadState.FAILURE);
		}
		return null;
	}
	
	public static void register()
	{
		NestedRoutine.register(ConditionalRoutine.class, Pattern.compile("(?:if|if_not).*", Pattern.CASE_INSENSITIVE));
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
				ModDamage.register(ConditionalRoutine.registeredConditionalStatements, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
		catch (IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}
	
	private static class FalseStatement extends ConditionalStatement
	{
		FalseStatement(){ super(false);}
		@Override
		public boolean condition(TargetEventInfo eventInfo){ return false;}
	}
}
