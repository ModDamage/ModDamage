package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityAirTicksComparison extends EntityComparison
{
	public EntityAirTicksComparison(boolean inverted, boolean forAttacker, int ticks, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, ticks, comparisonType);
	}
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return (eventInfo.getRelevantEntity(forAttacker) != null)?eventInfo.getRelevantEntity(forAttacker).getRemainingAir():-9001;}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityAirTicksComparison.class, Pattern.compile("(!?)(\\w+)\\.airticks\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAirTicksComparison getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ComparisonType comparisonType = ComparisonType.matchType(matcher.group(3));
			if(comparisonType != null)
				return new EntityAirTicksComparison(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, Integer.parseInt(matcher.group(4)), comparisonType);
		}
		return null;
	}
}
