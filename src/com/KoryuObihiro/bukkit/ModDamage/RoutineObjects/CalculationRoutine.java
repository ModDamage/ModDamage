package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.Calculate;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.ChangeProperty;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySpawn;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityUnknownHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.McMMOChangeSkill;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized.EntityItem;

abstract public class CalculationRoutine extends NestedRoutine 
{
	private static LinkedHashMap<Pattern, CalculationBuilder> registeredCalculations = new LinkedHashMap<Pattern, CalculationBuilder>();
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
		registeredCalculations.clear();
		NestedRoutine.registerRoutine(calculationPattern, new RoutineBuilder());
		registeredCalculations.clear();
		
		McMMOChangeSkill.register();

		
		Calculate.register();
		ChangeProperty.register();
		EntityItem.register();
		EntityExplode.register();
		EntityHurt.register();
		EntitySpawn.register();
		EntityUnknownHurt.register();
	}	

	public static void registerRoutine(Pattern syntax, CalculationBuilder builder)
	{
		Routine.registerRoutine(registeredCalculations, syntax, builder);
	}
	
	protected static final class RoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@Override
		public CalculationRoutine getNew(Matcher calculationMatcher, Object nestedContent)
		{
			if(calculationMatcher.group() != null && nestedContent != null)
			{
				NestedRoutine.paddedLogRecord(OutputPreset.INFO, "Calculation: \"" + calculationMatcher.group() + "\"");
				for(Pattern pattern : registeredCalculations.keySet())
				{
					Matcher matcher = pattern.matcher(calculationMatcher.group());
					if(matcher.matches())
					{
						List<Routine> routines = new ArrayList<Routine>();
						if(RoutineAliaser.parseRoutines(routines, nestedContent))
						{
							DynamicInteger match = DynamicInteger.getNew(routines);
							NestedRoutine.paddedLogRecord(OutputPreset.INFO_VERBOSE, "End Calculation \"" + calculationMatcher.group() + "\"");
							return registeredCalculations.get(pattern).getNew(matcher, match);
						}
						else
						{
							NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Bad content in Calculation \"" + calculationMatcher.group() + "\"");
							return null;
						}
					}
				}
				NestedRoutine.paddedLogRecord(OutputPreset.FAILURE, "Invalid Calculation \"" + calculationMatcher.group() + "\"");
			}
			return null;
		}
	}
	
	abstract protected static class CalculationBuilder
	{
		abstract public CalculationRoutine getNew(Matcher matcher, DynamicInteger integer);
	}
}
