package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public final class ChangeProperty extends CalculationRoutine<Object>
{	
	protected final IntegerMatch targetPropertyMatch;
	protected final boolean additive;
	public ChangeProperty(String configString, IntegerMatch value, IntegerMatch targetPropertyMatch, boolean additive)
	{
		super(configString, value);
		this.targetPropertyMatch = targetPropertyMatch;
		this.additive = additive;
	}
	
	@Override
	public void run(TargetEventInfo eventInfo)
	{
		targetPropertyMatch.setValue(eventInfo, value.getValue(eventInfo), additive);
	}

	@Override
	protected void applyEffect(Object affectedObject, int input){}

	@Override
	protected Object getAffectedObject(TargetEventInfo eventInfo){ return null;}
	
	public static void register()
	{
		CalculationRoutine.registerCalculation(ChangeProperty.class, Pattern.compile("(\\w+)effect\\.(set|add)(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static ChangeProperty getNew(Matcher matcher, IntegerMatch resultMatch)
	{
		if(matcher != null && resultMatch != null)
		{
			IntegerMatch targetPropertyMatch = IntegerMatch.getNew(matcher.group(1) + "." + matcher.group(3));
			if(targetPropertyMatch != null)
			{
				if(targetPropertyMatch.isSettable())
					return new ChangeProperty(matcher.group(), resultMatch, targetPropertyMatch, matcher.group(2).equalsIgnoreCase("add"));
				else ModDamage.addToLogRecord(DebugSetting.QUIET, "Error: Property \"" + matcher.group(3) + "\" of \"" + matcher.group(1) + "\" is not modifiable." , LoadState.FAILURE);
			}
		}
		return null;
	}
}
