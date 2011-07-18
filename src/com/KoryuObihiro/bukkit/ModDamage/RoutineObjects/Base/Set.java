package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Set extends Routine 
{
	private int setValue;
	public Set(int value){ setValue = value;}
	@Override
	public void run(DamageEventInfo eventInfo){ eventInfo.eventDamage = setValue;}
	@Override
	public void run(SpawnEventInfo eventInfo){ eventInfo.eventHealth = setValue;}
	
	public static Set getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Set(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register(RoutineUtility routineUtility)
	{
		routineUtility.registerBase(Set.class, Pattern.compile("set\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
