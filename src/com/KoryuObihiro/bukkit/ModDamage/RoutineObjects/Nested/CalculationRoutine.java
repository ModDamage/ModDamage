package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

abstract public class CalculationRoutine<AffectedClass> extends NestedRoutine 
{
	public static HashMap<Pattern, Method> registeredCalculations = new HashMap<Pattern, Method>();
	
	protected final List<Routine> routines;
	
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
	
	public static void register()
	{
		NestedRoutine.registerNested(CalculationRoutine.class, Pattern.compile("((?:([\\*\\w]+)effect\\." + RoutineAliaser.statementPart + "))", Pattern.CASE_INSENSITIVE));
	}
	
	public static CalculationRoutine<?> getNew(String string, Object nestedContent)
	{
		if(string != null && nestedContent != null)
		{
			ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
			ModDamage.addToLogRecord(DebugSetting.NORMAL, "Calculation: \"" + string + "\"", LoadState.SUCCESS);
			CalculationRoutine<?> routine = null;
			for(Pattern pattern : registeredCalculations.keySet())
			{
				Matcher matcher = pattern.matcher(string);
				if(matcher.matches())
				{
					LoadState[] stateMachine = { LoadState.SUCCESS };
					List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
					if(!stateMachine[0].equals(LoadState.FAILURE))
					{
						try 
						{
							routine = (CalculationRoutine<?>) registeredCalculations.get(pattern).invoke(null, matcher, routines);
						} 
						catch (Exception e){ e.printStackTrace();}
					}
				}
			}
			if(routine != null)
				ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Calculation \"" + string + "\"\n", LoadState.SUCCESS);
			else ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid Calculation \"" + string + "\"", LoadState.FAILURE);
			return routine;
		}
		return null;
	}

	public static void register(Class<? extends CalculationRoutine<?>> calculationClass, Pattern syntax)
	{
		try
		{
			Method method = calculationClass.getMethod("getNew", Matcher.class, List.class);
			if(method != null)//XXX Is this necessary?
			{
				assert(method.getReturnType().equals(calculationClass));
				method.invoke(null, (Matcher)null, (List<Routine>)null);
				ModDamage.register(CalculationRoutine.registeredCalculations, method, syntax);
			}
			else ModDamage.log.severe("Method getNew not found for nested routine " + calculationClass.getName());
		}
		catch(AssertionError e){ ModDamage.log.severe("[ModDamage] Error: getNew doesn't return class " + calculationClass.getName() + "!");}
		catch(SecurityException e){ ModDamage.log.severe("[ModDamage] Error: getNew isn't public for class " + calculationClass.getName() + "!");}
		catch(NullPointerException e){ ModDamage.log.severe("[ModDamage] Error: getNew for class " + calculationClass.getName() + " is not static!");}
		catch(NoSuchMethodException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have a getNew() method!");} 
		catch (IllegalArgumentException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have matching method getNew(Matcher, List)!");} 
		catch (IllegalAccessException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");} 
		catch (InvocationTargetException e){ ModDamage.log.severe("[ModDamage] Error: Class \"" + calculationClass.toString() + "\" does not have valid getNew() method!");}
	}
}
