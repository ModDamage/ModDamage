package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Set extends CalculationRoutine<Integer>
{
	protected final boolean usingStaticValue;
	protected int setValue;
	public Set(String configString, int value)
	{ 
		super(configString, null);
		usingStaticValue = true;
		setValue = value;
	}
	public Set(String configString, List<Routine> routines)
	{ 
		super(configString, routines);
		usingStaticValue = false;
		setValue = 0;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		if(usingStaticValue)
			eventInfo.eventValue = setValue;
		else eventInfo.eventValue = calculateInputValue(eventInfo);
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Set.class, Pattern.compile("set\\.(\\w+)", Pattern.CASE_INSENSITIVE));
		CalculationRoutine.registerStatement(routineUtility, Set.class, Pattern.compile("set", Pattern.CASE_INSENSITIVE));
	}
	
	public static Set getNew(Matcher matcher)
	{
		if(matcher != null)
			return new Set(matcher.group(), Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static Set getNew(Matcher matcher, List<Routine> routines)
	{ 
		if(matcher != null && routines != null)
			return new Set(matcher.group(), routines);
		return null;
	}
	@Override
	protected void applyEffect(Integer affectedObject, int input) {}
	@Override
	protected Integer getAffectedObject(TargetEventInfo eventInfo) { return null;}
}
