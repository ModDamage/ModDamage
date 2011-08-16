package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityFallComparison extends EntityComparison 
{
	public EntityFallComparison(boolean inverted, boolean forAttacker, int fallDistance, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, fallDistance, comparisonType);
	}
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return (int)eventInfo.getRelevantEntity(forAttacker).getFallDistance();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityFallComparison.class, Pattern.compile("(!)?(\\w+)\\.falldistance\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityFallComparison getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ComparisonType comparisonType = ComparisonType.matchType(matcher.group(3));
			if(comparisonType != null)
				return new EntityFallComparison(matcher.group(1) != null, (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, Integer.parseInt(matcher.group(4)), comparisonType);
		}
		return null;
	}
}
