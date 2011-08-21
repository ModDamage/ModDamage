package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Set extends CalculationRoutine<Integer>
{
	private int setValue;
	public Set(int value, List<Routine> routines)
	{ 
		super(routines);
		setValue = value;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = setValue;}
	
	public static void register(ModDamage routineUtility)
	{
		CalculationRoutine.registerStatement(routineUtility, Set.class, Pattern.compile("set", Pattern.CASE_INSENSITIVE));
	}
	
	public static Set getNew(Matcher matcher, List<Routine> routines)
	{ 
		if(matcher != null)
			return new Set(Integer.parseInt(matcher.group(1)), routines);
		return null;
	}
	@Override
	protected void applyEffect(Integer affectedObject, int input) {}
	@Override
	protected Integer getAffectedObject(TargetEventInfo eventInfo) { return null;}
}
