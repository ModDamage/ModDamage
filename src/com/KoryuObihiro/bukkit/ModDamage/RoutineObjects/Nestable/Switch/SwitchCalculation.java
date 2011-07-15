package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Switch;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional.ConditionalRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nestable.Conditional.ConditionalStatement;
import com.mysql.jdbc.AssertionFailedException;

abstract public class SwitchCalculation<InfoType> extends Routine 
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	public static final String statementPart = "(!(?:" + RoutineUtility.wordPart + ")(?:\\." + RoutineUtility.wordPart +")*)";
	final protected LinkedHashMap<InfoType, List<Routine>> switchStatements;
	
	public SwitchCalculation(LinkedHashMap<InfoType, List<Routine>> switchStatements)
	{
		this.switchStatements = switchStatements;
	}
	
	@Override
	public void run(DamageEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		if(info != null && switchStatements.containsKey(info))
			for(Routine calculation : switchStatements.get(info))
				calculation.run(eventInfo);
	}

	@Override
	public void run(SpawnEventInfo eventInfo) 
	{
		InfoType info = getRelevantInfo(eventInfo);
		if(info != null && switchStatements.containsKey(info))
			for(Routine calculation : switchStatements.get(info))
				calculation.run(eventInfo);
	}
	
	abstract protected InfoType getRelevantInfo(DamageEventInfo eventInfo);
	
	abstract protected InfoType getRelevantInfo(SpawnEventInfo eventInfo);
	
	public ConditionalRoutine getNew(Matcher matcher, List<Routine> calculations) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		List<String> statements = new ArrayList<String>();
		List<Routine> operations = new ArrayList<Routine>();
		
		/*
		for(int i = 0; i < matcher.groupCount(); i++)
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
		*/
		return null;
	}
	
	public static void register()
	{
		RoutineUtility.register(ConditionalRoutine.class, Pattern.compile(("if|if_not|if!)\\s" + statementPart + "" + "(?:\\s(" + RoutineUtility.logicalRegex + ")" + statementPart + ")*"), Pattern.CASE_INSENSITIVE));
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
}
