package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Division extends Routine 
{
	protected DynamicInteger divideValue;
	protected final boolean isAdditive;
	public Division(String configString, DynamicInteger value, boolean isAdditive)
	{
		super(configString);
		divideValue = value;
		this.isAdditive = isAdditive;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = isAdditive?eventInfo.eventValue:0 + eventInfo.eventValue/divideValue.getValue(eventInfo);}
	
	public static void register()
	{
		Routine.registerBase(Division.class, Pattern.compile("div(?:ide)?(_add)?\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static Division getNew(Matcher matcher)
	{ 
		if(matcher != null)
		{
			DynamicInteger match1 = DynamicInteger.getNew(matcher.group(2));
			if(match1 != null)
				return new Division(matcher.group(), match1, matcher.group(1) != null);
		}
		return null;
	}
}
