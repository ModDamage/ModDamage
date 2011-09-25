package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Nested.CalculationRoutine;

public class WorldTime extends WorldCalculationRoutine
{
	public WorldTime(String configString, List<Routine> routines)
	{
		super(configString, routines);
	}
	@Override
	protected World getAffectedObject(TargetEventInfo eventInfo){ return eventInfo.world;}
	@Override
	protected void applyEffect(World affectedObject, int input){ affectedObject.setFullTime(input);}

	public static void register() 
	{
		CalculationRoutine.register(WorldTime.class, Pattern.compile("worldeffect\\.setTime", Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldTime getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new WorldTime(matcher.group(), routines);
		return null;
	}
}