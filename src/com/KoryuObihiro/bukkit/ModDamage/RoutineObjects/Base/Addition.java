package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Addition extends Routine 
{	
	private DynamicInteger number;
	public Addition(String configString, DynamicInteger number)
	{
		super(configString);
		this.number = number;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue += number.getValue(eventInfo);}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile(DynamicInteger.dynamicIntegerPart, Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static final class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Addition getNew(Matcher matcher)
		{ 
			if(matcher != null)
			{
				DynamicInteger match = DynamicInteger.getNew(matcher.group(1));
				if(match != null)
				{
					ModDamage.addToLogRecord(OutputPreset.INFO, "Add: " + matcher.group(1));
					return new Addition(matcher.group(), match);
				}
			}
			return null;
		}
	}
}
