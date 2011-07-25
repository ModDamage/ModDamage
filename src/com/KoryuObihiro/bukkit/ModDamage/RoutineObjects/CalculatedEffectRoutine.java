package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;

abstract public class CalculatedEffectRoutine<AffectedClass> extends Routine 
{
	public static HashMap<Pattern, Method> registeredStatements = new HashMap<Pattern, Method>();
	final List<Routine> routines;
	final AffectedClass affectedObject;
	final boolean useEventObject;
	CalculatedEffectRoutine(AffectedClass affectedObject, List<Routine> routines)
	{
		this.routines = routines;
		this.affectedObject = affectedObject;
		this.useEventObject = true;
	}	
	protected CalculatedEffectRoutine(List<Routine> routines)
	{
		this.routines = routines;
		this.affectedObject = null;
		this.useEventObject = false;
	}
	
	@Override
	public void run(DamageEventInfo eventInfo)
	{
		AffectedClass someObject = (useEventObject?getAffectedObject(eventInfo):affectedObject);
		if(someObject != null)
			applyEffect(someObject, calculateInputValue(eventInfo));
	}

	@Override
	public void run(SpawnEventInfo eventInfo)
	{
		AffectedClass someObject = (useEventObject?getAffectedObject(eventInfo):affectedObject);
		if(someObject != null)
			applyEffect(someObject, calculateInputValue(eventInfo));
	}

	abstract protected void applyEffect(AffectedClass affectedObject, int input);

	abstract protected AffectedClass getAffectedObject(DamageEventInfo eventInfo);
	abstract protected AffectedClass getAffectedObject(SpawnEventInfo eventInfo);
	
	protected int calculateInputValue(DamageEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventDamage, temp2;
		eventInfo.eventDamage = 0;
		for(Routine routine : routines)
			routine.run(eventInfo);
		temp2 = eventInfo.eventDamage;
		eventInfo.eventDamage = temp1;
		return temp2;
	}

	protected int calculateInputValue(SpawnEventInfo eventInfo) 
	{
		int temp1 = eventInfo.eventHealth, temp2;
		eventInfo.eventHealth = 0;
		for(Routine routine : routines)
			routine.run(eventInfo);
		temp2 = eventInfo.eventHealth;
		eventInfo.eventHealth = temp1;
		return temp2;
	}

	public static CalculatedEffectRoutine<?> getNew(Matcher matcher, List<Routine> routines)
	{
		//parse all of the conditionals
		for(int i = 0; i < matcher.groupCount(); i += 2)
			for(Pattern pattern : registeredStatements.keySet())
			{
				Matcher statementMatcher = pattern.matcher(matcher.group(i));
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
	
	public static void registerStatement(RoutineUtility routineUtility, Class<? extends CalculatedEffectRoutine<?>> statementClass, Pattern syntax)
	{
		RoutineUtility.registerEffect(statementClass, syntax);
	}
}
