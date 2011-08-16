package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Conditional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.TargetEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ComparisonType;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.ConditionalRoutine;

public class EntityLightComparison extends EntityComparison
{
	public EntityLightComparison(boolean inverted, boolean forAttacker, int lightLevel, ComparisonType comparisonType) 
	{
		super(inverted, forAttacker, lightLevel, comparisonType);
	}
	@Override
	protected Integer getRelevantInfo(TargetEventInfo eventInfo){ return (int)eventInfo.getRelevantEntity(forAttacker).getLocation().getBlock().getLightLevel();}
	
	public static void register(ModDamage routineUtility)
	{
		ConditionalRoutine.registerStatement(routineUtility, EntityLightComparison.class, Pattern.compile("(!)?(\\w+)\\.light\\.(\\w+)\\.([0-9]+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityLightComparison getNew(Matcher matcher)
	{
		if(matcher != null)
		{
			ComparisonType comparisonType = ComparisonType.matchType(matcher.group(3));
			if(comparisonType != null)
				return new EntityLightComparison(matcher.group(1) != null, (ModDamage.matchesValidEntity(matcher.group(2)))?ModDamage.matchEntity(matcher.group(2)):false, Integer.parseInt(matcher.group(4)), comparisonType);
		}
		return null;
	}
}
