package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.World;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffectRoutine;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class WorldTime extends WorldCalculatedEffectRoutine
{
	public WorldTime(List<Routine> routines){ super(routines);}

	@Override
	protected World getAffectedObject(DamageEventInfo eventInfo){ return eventInfo.world;}
	@Override
	protected World getAffectedObject(SpawnEventInfo eventInfo){ return eventInfo.world;}

	@Override
	protected void applyEffect(World affectedObject, int input){ affectedObject.setFullTime(input);}

	public static void register(ModDamage modDamage) 
	{
		CalculatedEffectRoutine.registerStatement(modDamage, WorldTime.class, Pattern.compile("setWorldTime\\." + ModDamage.numberPart, Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldTime getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
		{
			return new WorldTime(routines);
		}
		return null;
	}
}

