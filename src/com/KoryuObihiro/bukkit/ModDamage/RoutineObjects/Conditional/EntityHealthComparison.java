package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityHealthComparison extends EntityComparison
{
	public EntityHealthComparison(boolean inverted, boolean forAttacker, int health, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, health, comparisonType);
	}
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return getRelevantEntity(eventInfo).getHealth();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityHealthComparison.class, Pattern.compile("(!)?" + ModDamage.entityPart + "health" + ModDamage.comparisonRegex + "([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityHealthComparison getNew(Matcher matcher)
	{
		if(matcher != null)
			return new EntityHealthComparison(matcher.group(1) != null, matcher.group(2).equalsIgnoreCase("attacker"), Integer.parseInt(matcher.group(4)), ComparisonType.matchType(matcher.group(3)));
		return null;
	}
}
