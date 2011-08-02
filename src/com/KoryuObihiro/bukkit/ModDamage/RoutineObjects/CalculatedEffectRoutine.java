package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

abstract public class CalculatedEffectRoutine<AffectedClass> extends Routine 
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	final List<Routine> routines;
	protected CalculatedEffectRoutine(List<Routine> routines)
	{
		this.routines = routines;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		AffectedClass someObject = getAffectedObject(eventInfo);
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

	public static CalculatedEffectRoutine<?> getNew(Matcher matcher, List<Routine> routines)
	{
		for(Pattern pattern : registeredStatements.keySet())
		{
			Matcher statementMatcher = pattern.matcher(matcher.group(1));
			if(statementMatcher.matches())
			{
				Method method = registeredStatements.get(pattern);
				//get next statement
				CalculatedEffectRoutine<?> statement = null;
				try 
				{
					statement = (CalculatedEffectRoutine<?>)method.invoke(null, statementMatcher, routines);
				}
				catch (Exception e){ e.printStackTrace();}
				return statement;
			}
		}
		return null;
	}
	
	public static void registerStatement(ModDamage routineUtility, Class<? extends CalculatedEffectRoutine<?>> statementClass, Pattern syntax)
	{
		ModDamage.registerEffect(statementClass, syntax);
	}
}
