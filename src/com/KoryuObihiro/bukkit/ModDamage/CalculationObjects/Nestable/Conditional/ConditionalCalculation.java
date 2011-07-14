package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.LogicalOperation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.NestedCalculation;
import com.mysql.jdbc.AssertionFailedException;

public class ConditionalCalculation extends NestedCalculation 
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	public static final String statementPart = "(!(?:" + CalculationUtility.wordPart + ")(?:\\." + CalculationUtility.wordPart +")*)";
	//For every statement, there must be a relation after it. Merely cap the last one with AND(asdf, true);
	final protected boolean inverted;
	final List<ConditionalStatement> statements;
	final List<LogicalOperation> logicalOperations;
	public ConditionalCalculation(boolean inverted, List<ConditionalStatement> statements, List<LogicalOperation> logicalOperations, List<ModDamageCalculation> calculations)
	{
		super(calculations);
		this.inverted = inverted;
		this.statements = statements;
		this.logicalOperations = logicalOperations;
	}
	@Override
	public void calculate(DamageEventInfo eventInfo)
	{
		boolean result = statements.get(0).condition(eventInfo);
		for(int i = 1; i < statements.size(); i++)
			 result = logicalOperations.get(i).operate(result, statements.get(i).condition(eventInfo));
		if(inverted?!result:result) doCalculations(eventInfo);
	}
	
	@Override
	public void calculate(SpawnEventInfo eventInfo)
	{
		boolean result = statements.get(0).condition(eventInfo);
		for(int i = 1; i < statements.size(); i++)
			 result = logicalOperations.get(i).operate(result, statements.get(i).condition(eventInfo));
		if(inverted?!result:result) doCalculations(eventInfo);
	}
	
	public ConditionalCalculation getNew(Matcher matcher, List<ModDamageCalculation> calculations) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
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
					ConditionalStatement statement = (ConditionalStatement)method.invoke(null, statementMatcher);
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
	
	public static void register()
	{
		CalculationUtility.register(ConditionalCalculation.class, Pattern.compile(("if|if_not|if!)\\s" + statementPart + "" + "(?:\\s(" + CalculationUtility.logicalRegex + ")" + statementPart + ")*"), Pattern.CASE_INSENSITIVE));
	}
	
	public static void registerStatement(Class<? extends ConditionalStatement> statementClass, Pattern syntax)
	{
		Logger log = Logger.getLogger("Minecraft");//FIXME
		boolean successfullyRegistered = false;
		if(syntax != null)
		{
			try
			{
				Method method = statementClass.getMethod("getNew", Matcher.class);
				if(method != null)
				{
					assert(method.getReturnType().equals(statementClass));
					method.invoke(null, (Matcher)null);
					registeredStatements.put(syntax, method);
					successfullyRegistered = true;
				}
				else log.severe("Method getNew not found for statement " + statementClass.getName());
			}
			catch(AssertionFailedException e){ log.severe("[ModDamage] Error: getNew doesn't return statement " + statementClass.getName() + "!");}
			catch(SecurityException e){ log.severe("[ModDamage] Error: getNew isn't public for statement " + statementClass.getName() + "!");}
			catch(NullPointerException e){ log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
			catch(NoSuchMethodException e){ log.severe("[ModDamage] Error: Statement class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
			catch (IllegalArgumentException e){ log.severe("[ModDamage] Error: Statement class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher)!");} 
			catch (IllegalAccessException e){ log.severe("[ModDamage] Error: Statement class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
			catch (InvocationTargetException e){ log.severe("[ModDamage] Error: Statement class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 	
		}
		else log.severe("[ModDamage] Error: Bad regex in calculation class \"" + statementClass.toString() + "\"!");
		if(successfullyRegistered)
		{
			if(ModDamage.consoleDebugging_verbose) log.info("[ModDamage] Registering calculation " + statementClass.toString() + " with pattern " + syntax.pattern());
		}
	}
	
	private class FalseStatement extends ConditionalStatement
	{
		FalseStatement(){ super(false);}
		@Override
		public boolean condition(DamageEventInfo eventInfo){ return false;}
		@Override
		public boolean condition(SpawnEventInfo eventInfo){ return false;}
	}
}
