package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Addition extends Routine 
{	
	private int addValue;
	public Addition(int value){ addValue = value;}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue += addValue;}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Addition.class, Pattern.compile("([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static Addition getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Addition(Integer.parseInt(matcher.group(1)));
		return null;
	}
}
