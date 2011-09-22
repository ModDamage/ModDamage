package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.CalculationRoutine;

public abstract class NestedRoutine extends Routine
{
	public static HashMap<Pattern, Method> registeredNestedRoutines = new HashMap<Pattern, Method>();

	protected NestedRoutine(String configString)
	{
		super(configString);
	}
	
	/*public static <T> T f(T x)*///FIXME USE A GENERIC, get rid of repetitive code elsewhere.
	public static void register(Class<? extends NestedRoutine> statementClass, Pattern syntax)
	{
		try
		{
			Method method = statementClass.getMethod("getNew", String.class, Object.class);
			if(method != null)//XXX Is this necessary?
			{
				assert(method.getReturnType().equals(statementClass));
				method.invoke(null, (String)null, (Object)null);
				ModDamage.register(CalculationRoutine.registeredNestedRoutines, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for nested routine " + statementClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + statementClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + statementClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + statementClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have matching method getNew(Matcher, List)!");} 
		catch (IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + statementClass.toString() + "\" does not have valid getNew() method!");}
	}

	public static NestedRoutine getNew(String string, Object nestedContent)
	{
		for(Pattern pattern : registeredNestedRoutines.keySet())
		{
			Matcher matcher = pattern.matcher(string);
			if(matcher.matches())
			{
				Method method = registeredNestedRoutines.get(pattern);
				//get next statement
				try 
				{
					return (NestedRoutine)method.invoke(null, string, nestedContent);
				}
				catch (Exception e){ e.printStackTrace();}
				return null;
			}
		}
		ModDamage.addToLogRecord(DebugSetting.QUIET, " No match found for nested routine \"" + string + "\"", LoadState.FAILURE);		
		return null;
	}
}
