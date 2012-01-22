package com.ModDamage.Routines.Nested;

import java.util.regex.Matcher;

import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.Routines.Routines;
import com.ModDamage.Routines.Nested.Calculation.EntityExplode;
import com.ModDamage.Routines.Nested.Calculation.EntityHeal;
import com.ModDamage.Routines.Nested.Calculation.EntityHurt;
import com.ModDamage.Routines.Nested.Calculation.EntityUnknownHurt;
import com.ModDamage.Routines.Nested.Calculation.McMMOChangeSkill;
import com.ModDamage.Routines.Nested.Calculation.OldChangeProperty;

abstract public class CalculationRoutine extends NestedRoutine 
{
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

		OldChangeProperty.register();
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
				
				Routines routines = RoutineAliaser.parseRoutines(nestedContent);
				if(routines != null)
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
