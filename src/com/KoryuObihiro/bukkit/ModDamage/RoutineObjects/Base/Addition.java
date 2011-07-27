package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Addition extends Routine 
{	
	private int addValue;
	public Addition(int value){ addValue = value;}
	@Override
	public void run(DamageEventInfo eventInfo){ eventInfo.eventDamage += addValue;}
	@Override
	public void run(SpawnEventInfo eventInfo){ eventInfo.eventHealth += addValue;}
	
	public static Addition getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Addition(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Addition.class, Pattern.compile("([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
