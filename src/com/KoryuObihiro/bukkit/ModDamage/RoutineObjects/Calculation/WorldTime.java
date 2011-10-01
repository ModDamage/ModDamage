package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class WorldTime extends WorldCalculationRoutine
{
	public WorldTime(String configString, IntegerMatch match)
	{
		super(configString, match);
	}
	@Override
	protected World getAffectedObject(TargetEventInfo eventInfo){ return eventInfo.world;}
	@Override
	protected void applyEffect(World affectedObject, int input){ affectedObject.setFullTime(input);}

	public static void register() 
	{
		CalculationRoutine.registerCalculation(WorldTime.class, Pattern.compile("worldeffect\\.setTime", Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldTime getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null)
			return new WorldTime(matcher.group(), match);
		return null;
	}
}