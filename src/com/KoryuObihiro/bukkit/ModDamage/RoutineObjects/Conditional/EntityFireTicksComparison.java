package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityFireTicksComparison extends EntityComparison
{
	public EntityFireTicksComparison(boolean inverted, boolean forAttacker, int ticks, ComparisonType comparisonType)
	{ 
		super(inverted, forAttacker, ticks, comparisonType);
	}
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return eventInfo.getRelevantEntity(forAttacker).getFireTicks();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityFireTicksComparison.class, Pattern.compile("(!?)(\\w+)\\.fireticks\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityFireTicksComparison getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ComparisonType comparisonType = ComparisonType.matchType(matcher.group(3));
			if(comparisonType != null)
				return new EntityFireTicksComparison(matcher.group(1).equalsIgnoreCase("!"), (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, Integer.parseInt(matcher.group(4)), comparisonType);
		}
		return null;
	}
}