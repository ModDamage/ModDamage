package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class CalculationRoutine<AffectedClass> extends Routine 
{
	public static HashMap<Pattern, Method> registeredRoutines = new HashMap<Pattern, Method>();
	final List<Routine> routines;
	protected CalculationRoutine(String configString, List<Routine> routines)
	{
		super(configString);
		this.routines = routines;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		AffectedClass someObject = (AffectedClass)getAffectedObject(eventInfo);
		if(someObject != null)
			applyEffect(someObject, calculateInputValue(eventInfo));
	}

	abstract protected void applyEffect(AffectedClass affectedObject, int input);

	abstract protected AffectedClass getAffectedObject(TargetEventInfo eventInfo);

	protected int calculateInputValue(TargetEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventValue, temp2;
		eventInfo.eventValue = 0;
		for(Routine routine : routines)
			routine.run(eventInfo);
		temp2 = eventInfo.eventValue;
		eventInfo.eventValue = temp1;
		return temp2;
	}

	public static CalculationRoutine<?> getNew(Matcher matcher, List<Routine> routines)
	{
		for(Pattern pattern : registeredRoutines.keySet())
		{
			Matcher statementMatcher = pattern.matcher(matcher.group(1));
			if(statementMatcher.matches())
			{
				Method method = registeredRoutines.get(pattern);
				//get next statement
				CalculationRoutine<?> statement = null;
				try 
				{
					statement = (CalculationRoutine<?>)method.invoke(null, statementMatcher, routines);
				}
				catch (Exception e){ e.printStackTrace();}
				return statement;
			}
		}
		return null;
	}
	
	public static void registerStatement(Class<? extends CalculationRoutine<?>> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", Matcher.class, List.class);
			if(method != null)//XXX Is this necessary?
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (Matcher)null, (List<Routine>)null);
				ModDamage.register(CalculationRoutine.registeredRoutines, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for statement " + statementClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher, List)!");} 
		catch (IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
	}
}
