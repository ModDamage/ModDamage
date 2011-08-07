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
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return (getRelevantEntity(eventInfo) instanceof Slime)?((Slime)getRelevantEntity(eventInfo)).getSize():0;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, SlimeSizeComparison.class, Pattern.compile("(!)?" + ModDamage.entityRegex + "size\\." + ModDamage.comparisonRegex + "\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static SlimeSizeComparison getNew(Matcher matcher)
	{
		if(matcher != null)
			return new SlimeSizeComparison(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), Integer.parseInt(matcher.group(4)), ComparisonType.matchType(matcher.group(3)));
		return null;
	}
}
