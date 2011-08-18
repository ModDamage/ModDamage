package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Slime;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class SlimeSizeComparison extends EntityComparison
{
	public SlimeSizeComparison(boolean inverted, boolean forAttacker, int value, ComparisonType comparisonType) 
	{
		super(inverted, forAttacker, value, comparisonType);
	}
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return (eventInfo.getRelevantEntity(forAttacker) instanceof Slime)?((Slime)eventInfo.getRelevantEntity(forAttacker)).getSize():0;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, SlimeSizeComparison.class, Pattern.compile("(!?)(\\w+)\\.size\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static SlimeSizeComparison getNew(Matcher matcher)
	{
		if(matcher != null)
			return new SlimeSizeComparison(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, Integer.parseInt(matcher.group(4)), ComparisonType.matchType(matcher.group(3)));
		return null;
	}
}