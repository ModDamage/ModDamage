package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class Addition extends ModDamageCalculation 
{	
	private int addValue;
	public Addition(int value){ addValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage += addValue;}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth += addValue;}
	
	public static Addition getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new Addition(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register(){ CalculationUtility.register(Addition.class, Pattern.compile("([0-9+)", Pattern.CASE_INSENSITIVE));}
}
