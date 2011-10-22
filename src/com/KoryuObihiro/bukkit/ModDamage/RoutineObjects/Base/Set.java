package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.NestedRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Set extends CalculationRoutine
{
	public Set(String configString, DynamicInteger value)
	{ 
		super(configString, value);
	}
	
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = value.getValue(eventInfo);}
	
	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input){}
	
	public static void register()
	{
		Routine.registerBase(Set.class, Pattern.compile("set\\." + DynamicInteger.dynamicPart, Pattern.CASE_INSENSITIVE));
		NestedRoutine.registerNested(Set.class, Pattern.compile("set", Pattern.CASE_INSENSITIVE));
	}
	
	public static Set getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(1));
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
			{
				DynamicInteger match = DynamicInteger.getNew(routines);
				if(match != null)
					return new Set(string, match);
			}
		}
		return null;
	}
}