package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class WorldTime extends WorldCalculatedEffectRoutine
{
	public WorldTime(List<Routine> routines){ super(routines);}
	@Override
	protected World getAffectedObject(TargetEventInfo eventInfo){ return eventInfo.world;}
	@Override
	protected void applyEffect(World affectedObject, int input){ affectedObject.setFullTime(input);}

	public static void register(ModDamage modDamage) 
	{
		CalculationRoutine.registerStatement(modDamage, WorldTime.class, Pattern.compile("worldeffect\\.setTime", Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldTime getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
			return new WorldTime(routines);
		return null;
	}
}

