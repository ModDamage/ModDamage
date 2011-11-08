package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Aliasing.RoutineAliaser;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.Calculate;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.ChangeProperty;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityDropItem;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityExplode;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityHurt;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntitySpawn;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation.EntityUnknownHurt;

abstract public class CalculationRoutine extends NestedRoutine 
{
	private static HashMap<Pattern, CalculationBuilder> registeredCalculations = new HashMap<Pattern, CalculationBuilder>();
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
		//Routine.registerBase(CalculationRoutine.class, calculationPattern); FIXME 0.9.6
		registeredCalculations.clear();
		NestedRoutine.registerRoutine(calculationPattern, new RoutineBuilder());
		registeredCalculations.clear();
		Calculate.register();
		ChangeProperty.register();
		EntityDropItem.register();
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
				ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "Calculation: \"" + calculationMatcher.group() + "\"", LoadState.SUCCESS);
				for(Pattern pattern : registeredCalculations.keySet())
				{
					Matcher matcher = pattern.matcher(calculationMatcher.group());
					if(matcher.matches())
					{
						ModDamage.indentation++;
						LoadState[] stateMachine = { LoadState.SUCCESS };
						List<Routine> routines = RoutineAliaser.parse(nestedContent, stateMachine);
						ModDamage.indentation--;
						if(!stateMachine[0].equals(LoadState.FAILURE))
						{
							DynamicInteger match = DynamicInteger.getNew(routines);
							ModDamage.addToLogRecord(DebugSetting.VERBOSE, "End Calculation \"" + matcher.group() + "\"", LoadState.SUCCESS);
							ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
							return registeredCalculations.get(pattern).getNew(matcher, match);
						}
					}
				}
				ModDamage.addToLogRecord(DebugSetting.QUIET, "Invalid Calculation \"" + calculationMatcher.group() + "\"", LoadState.FAILURE);
				ModDamage.addToLogRecord(DebugSetting.CONSOLE, "", LoadState.SUCCESS);
			}
			return null;
		}
	}
	
	abstract protected static class CalculationBuilder
	{
		abstract public CalculationRoutine getNew(Matcher matcher, DynamicInteger integer);
	}
}
