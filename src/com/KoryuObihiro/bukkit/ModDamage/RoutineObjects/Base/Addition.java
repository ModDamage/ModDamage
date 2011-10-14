package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicInteger;
import com.KoryuObihiro.bukkit.ModDamage.Backend.Matching.DynamicString;
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
		Routine.registerBase(Addition.class, Pattern.compile(DynamicString.dynamicPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static Addition getNew(Matcher matcher)
	{ 
		if(matcher != null)
		{
			DynamicInteger match = DynamicInteger.getNew(matcher.group(1));
			if(match != null)
				return new Addition(matcher.group(), match);
		}
		return null;
	}
}
