package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class Multiplication extends Routine 
{
	private int multiplicationValue;
	public Multiplication(int value){ multiplicationValue = value;}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue *= multiplicationValue;}
	
	public static Multiplication getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Multiplication(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(Multiplication.class, Pattern.compile("mult\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
