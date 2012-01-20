package com.ModDamage.RoutineObjects.Nested.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.RoutineObjects.Nested.CalculationRoutine;
import com.ModDamage.RoutineObjects.Nested.NestedRoutine;

public final class ChangeProperty extends CalculationRoutine
{	
	protected final DynamicInteger targetPropertyMatch;
	protected final boolean additive;
	protected final boolean oldStyle;
	public ChangeProperty(String configString, DynamicInteger value, DynamicInteger targetPropertyMatch, boolean additive, boolean oldStyle)
	{
		super(configString, value);
		this.targetPropertyMatch = targetPropertyMatch;
		this.additive = additive;
		this.oldStyle = oldStyle;
	}

	@Override
	protected void doCalculation(TargetEventInfo eventInfo, int input)
	{
		targetPropertyMatch.setValue(eventInfo, value.getValue(eventInfo) + (additive?targetPropertyMatch.getValue(eventInfo):0));
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		int eventValue = eventInfo.eventValue;
		if (!oldStyle)
		{
			eventInfo.eventValue = targetPropertyMatch.getValue(eventInfo);
			value.getValue(eventInfo); // TODO when oldStyle deprecated is removed, switch ChangeProperty to a NestedRoutine
			targetPropertyMatch.setValue(eventInfo, eventInfo.eventValue);
		}
		else
		{
			doCalculation(eventInfo, value.getValue(eventInfo));
		}
		eventInfo.eventValue = eventValue;
	}
	
	public static void register()
	{
		CalculationRoutine.registerRoutine(Pattern.compile("(.*)effect\\.(set|add)(.*)|set.(.*)", Pattern.CASE_INSENSITIVE), new CalculationRoutine.CalculationBuilder()
				{	
					@Override
					public ChangeProperty getNew(Matcher matcher, DynamicInteger routines)
					{
						DynamicInteger targetPropertyMatch;
						boolean oldStyle;
						boolean additive;
						if (matcher.group(4) != null)
						{
							oldStyle = false; additive = false;
							targetPropertyMatch = DynamicInteger.getNew(matcher.group(4));
						}
						else
						{
							oldStyle = true;
							NestedRoutine.paddedLogRecord(OutputPreset.WARNING_STRONG, "This form is deprecated. Please use 'set."
									+matcher.group(1) + "_" + matcher.group(3)+"' instead.");
							
							targetPropertyMatch = DynamicInteger.getNew(matcher.group(1) + "_" + matcher.group(3));
							additive = matcher.group(2).equalsIgnoreCase("add");
						}
						if(targetPropertyMatch != null)
						{
							if(targetPropertyMatch.isSettable())
								return new ChangeProperty(matcher.group(), routines, targetPropertyMatch, additive, oldStyle);
							else
								ModDamage.addToLogRecord(OutputPreset.FAILURE, "Error: Property \"" + targetPropertyMatch.toString() + "\" is not modifiable.");
						}
						return null;
					}
				});
	}
}
