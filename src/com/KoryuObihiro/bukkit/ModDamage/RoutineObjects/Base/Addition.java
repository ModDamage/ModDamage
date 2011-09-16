package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Addition extends Routine 
{	
	private IntegerMatch number;
	public Addition(String configString, IntegerMatch number)
	{
		super(configString);
		this.number = number;
	}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue += number.getValue(eventInfo);}
	
	public static void register(ModDamage routineUtility)
	{
		Routine.registerBase(Addition.class, Pattern.compile("(-?[0-9]+|(\\w+\\.\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static Addition getNew(Matcher matcher)
	{ 
		if(matcher != null)
		{
			IntegerMatch match = IntegerMatch.getNew(matcher.group(1));
			return new Addition(matcher.group(), match);
		}
		return null;
	}
}
