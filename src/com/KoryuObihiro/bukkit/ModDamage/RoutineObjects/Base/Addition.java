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
		Routine.registerRoutine(Pattern.compile("(?:add\\.|(?:\\+|\\-)[^\\+\\-]).+", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	protected static final class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Addition getNew(Matcher matcher)
		{ 
			if(matcher != null)
			{
				String string = matcher.group().startsWith("+")?matcher.group().substring(1):matcher.group();
				if(string.toLowerCase().startsWith("add."))
					string = string.substring(4);
				DynamicInteger match = DynamicInteger.getNew(string);
				if(match != null)
				{
					ModDamage.addToLogRecord(OutputPreset.INFO, "Add: " + matcher.group());
					return new Addition(matcher.group(), match);
				}
			}
			return null;
		}
	}
}
