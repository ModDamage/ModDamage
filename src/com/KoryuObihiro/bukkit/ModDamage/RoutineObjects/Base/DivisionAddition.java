package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class DivisionAddition extends Routine 
{
	private int divideValue;
	public DivisionAddition(int value){ divideValue = (value != 0?value:1);}
	@Override
	public void run(DamageEventInfo eventInfo){ eventInfo.eventDamage += eventInfo.eventDamage/divideValue;}
	@Override
	public void run(SpawnEventInfo eventInfo){ eventInfo.eventHealth += eventInfo.eventHealth/divideValue;}
	
	public static DivisionAddition getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new DivisionAddition(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register()
	{
		RoutineUtility.register(DivisionAddition.class, Pattern.compile("div_add\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
