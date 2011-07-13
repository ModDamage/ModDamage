package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;

public class DiceRollAddition extends ChanceCalculation 
{
	private int maxValue;
	public DiceRollAddition(int value){ maxValue = value;}
	@Override
	public void calculate(DamageEventInfo eventInfo){ eventInfo.eventDamage += Math.abs(random.nextInt()%(maxValue + 1));}
	@Override
	public void calculate(SpawnEventInfo eventInfo){ eventInfo.eventHealth += Math.abs(random.nextInt()%(maxValue + 1));}
	
	public static DiceRollAddition getNew(Matcher matcher)
	{ 
		if(matcher != null)
			return new DiceRollAddition(Integer.parseInt(matcher.group(1)));
		return null;
	}
	
	public static void register()
	{
		CalculationUtility.register(DiceRollAddition.class, Pattern.compile("roll\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
}