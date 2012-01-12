package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation.ChangeProperty;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation.EntityHeal;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation.EntityHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation.EntityItemAction;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation.EntityUnknownHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.Calculation.McMMOChangeSkill;

abstract public class CalculationRoutine extends NestedRoutine 
{
	//private static LinkedHashMap<Pattern, CalculationBuilder> registeredCalculations = new LinkedHashMap<Pattern, CalculationBuilder>();
	
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
		McMMOChangeSkill.register();

		ChangeProperty.register();
		EntityItemAction.register();
		EntityExplode.register();
		EntityHeal.register();
		EntityHurt.register();
		EntityUnknownHurt.register();
	}
	
	protected static abstract class CalculationBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public final CalculationRoutine getNew(Matcher matcher, Object nestedContent)
		{
			if(matcher.group() != null && nestedContent != null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Calculation: \"" + matcher.group() + "\"");
				
				List<Routine> routines = new ArrayList<Routine>();
				if(RoutineAliaser.parseRoutines(routines, nestedContent))
				{
					DynamicInteger integer = DynamicInteger.getNew(routines);
					CalculationRoutine calc = getNew(matcher, integer);
					NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End Calculation \"" + matcher.group() + "\"");
					return calc;
				}
				else
				{
					NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Bad content in Calculation \"" + matcher.group() + "\"");
				}
			}
			return null;
		}
		
		abstract public CalculationRoutine getNew(Matcher matcher, DynamicInteger integer);
	}
}
