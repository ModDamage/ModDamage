package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Set extends Routine 
{
	private int setValue;
	public Set(int value){ setValue = value;}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = setValue;}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Set.class, Pattern.compile("set\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static Set getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Set(Integer.parseInt(matcher.group(1)));
		return null;
	}
}