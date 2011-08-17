package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class WorldTimeComparison extends WorldConditionalStatement
{
	final int value;
	final ComparisonType comparisonType;
	public WorldTimeComparison(boolean inverted, ComparisonType comparisonType, int value)
	{
		super(inverted);
		this.comparisonType = comparisonType;
		this.value = value;
	}
	@Override
	public boolean condition(TargetEventInfo eventInfo){ return comparisonType.compare((int) eventInfo.world.getTime(), value);}//FIXME Make sure this works.
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, WorldTimeComparison.class, Pattern.compile("(!?)worldtime\\.(\\w+)\\.([0-9]{1,5})", Pattern.CASE_INSENSITIVE));
	}
	
	public static WorldTimeComparison getNew(Matcher matcher)
	{
		if(matcher != null)
			return new WorldTimeComparison(matcher.group(1).equalsIgnoreCase("!"), ComparisonType.matchType(matcher.group(2)), Integer.parseInt(matcher.group(3)));
		return null;
	}
}
