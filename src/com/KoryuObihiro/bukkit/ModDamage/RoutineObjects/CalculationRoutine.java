package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.Calculate;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.ChangeProperty;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityDropItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySpawn;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityUnknownHurt;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;

abstract public class CalculationRoutine extends NestedRoutine 
{
	private static HashMap<Pattern, Method> registeredCalculations = new HashMap<Pattern, Method>();
	protected final static Pattern calculationPattern = Pattern.compile("((?:([\\*\\w]+)effect\\." + Routine.statementPart + "))", Pattern.CASE_INSENSITIVE);
	
	protected final DynamicInteger value;
	
	protected CalculationRoutine(String configString, DynamicInteger value)
	{
		super(configString);
		this.value = value;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		int eventValue = eventInfo.eventValue;
			doCalculation(eventInfo, value.getValue(eventInfo));
		eventInfo.eventValue = eventValue;
	}

	abstract protected void doCalculation(TargetEventInfo eventInfo, int input);
	
	public static void register()
	{
		//Routine.registerBase(CalculationRoutine.class, calculationPattern); FIXME 0.9.6
		registeredCalculations.clear();
		NestedRoutine.registerNested(CalculationRoutine.class, calculationPattern);
		registeredCalculations.clear();
		Calculate.register();
		ChangeProperty.register();
		EntityDropItem.register();
		EntityExplode.register();
		EntityHurt.register();
		EntitySpawn.register();
		EntityUnknownHurt.register();
	}
	
	public static CalculationRoutine getNew(String string, Object nestedContent)
	{
		if(string != null && nestedContent != null)
		{
			ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
			ModDamage.addToLogRecord(DebugSetting.NORMAL, "Calculation: \"" + string + "\"", LoadState.SUCCESS);
			for(Pattern pattern : registeredCalculations.keySet())
			{
				Matcher matcher = pattern.matcher(string);
				if(matcher.matches())
				{
					ModDamage.indentation++;
					LoadState[] stateMachine = { LoadState.SUCCESS };
					List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
					ModDamage.indentation--;
					if(!stateMachine[0].equals(LoadState.FAILURE))
					{
						DynamicInteger match = DynamicInteger.getNew(routines);
						if(match != null)
						{
							try 
							{
								ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Calculation \"" + string + "\"", LoadState.SUCCESS);
								ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
								return (CalculationRoutine)registeredCalculations.get(pattern).invoke(null, matcher, match);
							} 
							catch (Exception e){ e.printStackTrace();}
						}
					}
				}
			}
			ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid Calculation \"" + string + "\"", LoadState.FAILURE);
			ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
		}
		return null;
	}

	public static void registerCalculation(Class<? extends CalculationRoutine> calculationClass, Pattern syntax)
	{
		try
		{
			Method method = calculationClass.getMethod("getNew", Matcher.class, DynamicInteger.class);
			if(method != null)//XXX Is this necessary?
			{
				assert(method.getReturnType().equals(calculationClass));
				method.invoke(null, (Matcher)null, (DynamicInteger)null);
				Routine.register(CalculationRoutine.registeredCalculations, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for nested routine " + calculationClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + calculationClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + calculationClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + calculationClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have a getNew() method!");} 
		catch(IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have matching method getNew(Matcher, IntegerMatch)!");} 
		catch(IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");} 
		catch(InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");}
	}
}
