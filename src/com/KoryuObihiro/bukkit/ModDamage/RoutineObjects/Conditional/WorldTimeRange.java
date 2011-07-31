package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class WorldTimeRange extends WorldConditionalStatement
{
	private final int beginningTime;
	private final int endTime;

	public WorldTimeRange(boolean inverted, int beginningTime, int endTime)
	{
		super(beginningTime > endTime | inverted);
		this.beginningTime = beginningTime;
		this.endTime = endTime;
	}
	
	@Override
	public boolean condition(DamageEventInfo eventInfo){ return (world.getTime() > beginningTime && world.getTime() < endTime);}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return (world.getTime() > beginningTime && world.getTime() < endTime);}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, WorldTimeRange.class, Pattern.compile("(!?)world\\.time\\.([0-9]{1,5})\\.([0-9]{1,5})", Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldTimeRange getNew(Matcher matcher)
	{
		if(matcher != null)
			return new WorldTimeRange(matcher.group(1) != null, Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
		return null;
	}
}
