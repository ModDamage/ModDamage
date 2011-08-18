package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Division extends Routine 
{
	private int divideValue;
	public Division(int value){ divideValue = value;}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = eventInfo.eventValue/divideValue;}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Division.class, Pattern.compile("div\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static Division getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Division(Integer.parseInt(matcher.group(1)));
		return null;
	}
}