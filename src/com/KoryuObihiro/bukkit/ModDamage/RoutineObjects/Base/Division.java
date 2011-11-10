package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.DebugSetting;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage.LoadState;
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
		Routine.registerRoutine(Pattern.compile("div(?:ide)?(_add)?\\." + DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Division getNew(Matcher matcher)
		{ 
			DynamicInteger match = DynamicInteger.getNew(matcher.group(2));
			if(match != null)
			{
				ModDamage.addToLogRecord(DebugSetting.NORMAL, "Division: " + matcher.group(1), LoadState.SUCCESS);
				return new Division(matcher.group(), match, matcher.group(1) != null);
			}
			return null;
		}
	}
}
