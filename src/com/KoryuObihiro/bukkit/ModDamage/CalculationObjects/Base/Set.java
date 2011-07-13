package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Set extends ModDamageCalculation 
{
	private int setValue;
	public Set(int value){ setValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage = setValue;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth = setValue;}
	
	public static Set getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Set(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register()
	{
		CalculationUtility.register(Set.class, Pattern.compile("set\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}
