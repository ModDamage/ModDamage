package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;

public class DiceRoll extends Chanceroutine 
{
	public DiceRoll(){}
	@Override
	public void run(TargetEventInfo eventInfo){ eventInfo.eventValue = Math.abs(random.nextInt()%(eventInfo.eventValue + 1));}
	
	public static DiceRoll getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new DiceRoll();
		return null;
	}
	
	public static void register(ModDamage routineUtility)
	{
		routineUtility.registerBase(DiceRoll.class, Pattern.compile("roll", Pattern.CASE_INSENSITIVE));
	}
}