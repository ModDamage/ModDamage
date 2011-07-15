package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.RoutineUtility;

public class DiceRoll extends ChanceCalculation 
{
	public DiceRoll(){}
	@Override
	public void run(DamageEventInfo eventInfo){ eventInfo.eventDamage = Math.abs(random.nextInt()%(eventInfo.eventDamage + 1));}
	@Override
	public void run(SpawnEventInfo eventInfo){ eventInfo.eventHealth = Math.abs(random.nextInt()%(eventInfo.eventHealth + 1));}
	
	public static DiceRoll getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new DiceRoll();
		return null;
	}
	
	public static void register()
	{
		RoutineUtility.register(DiceRoll.class, Pattern.compile("roll", Pattern.CASE_INSENSITIVE));
	}
}