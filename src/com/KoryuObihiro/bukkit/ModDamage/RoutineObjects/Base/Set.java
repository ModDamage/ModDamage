package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class Set extends CalculationRoutine<Integer>
{
	protected final boolean usingStaticValue;
	protected IntegerMatch setValue;
	public Set(String configString, IntegerMatch value)
	{ 
		super(configString, null);
		usingStaticValue = true;
		setValue = value;
	}
	public Set(String configString, List<Routine> routines)
	{ 
		super(configString, routines);
		usingStaticValue = false;
		setValue = null;
	}
	@Override
	public void run(TargetEventInfo eventInfo)
	{ 
		if(usingStaticValue)
			eventInfo.eventValue = setValue.getValue(eventInfo);
		else eventInfo.eventValue = calculateInputValue(eventInfo);
	}
	
	public static void register()
	{
		Routine.registerBase(Set.class, Pattern.compile("set\\." + Routine.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
		NestedRoutine.registerNested(Set.class, Pattern.compile("set", Pattern.CASE_INSENSITIVE));
	}
	
	public static Set getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			IntegerMatch match1 = IntegerMatch.getNew(matcher.group(1));
			if(match1 != null)
				return new Set(matcher.group(), match1);
		}
		return null;
	}
	
	public static Set getNew(String string, Object nestedContent)
	{ 
		if(string != null && nestedContent != null)
		{
			LoadState[] stateMachine = { LoadState.SUCCESS };
			List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
			if(!stateMachine[0].equals(LoadState.FAILURE))
				return new Set(string, routines);
		}
		return null;
	}
	@Override
	protected void applyEffect(Integer affectedObject, int input) {}
	@Override
	protected Integer getAffectedObject(TargetEventInfo eventInfo) { return null;}
}